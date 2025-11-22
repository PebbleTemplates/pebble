(function ($, lunr) {

    function initializeSearchField() {
        $('form[data-orchid-search]').submit(function (e) {
            e.preventDefault();

            var $queryEl = $(this).find("input[name=query]");
            var $query = $queryEl.val();
            var $orchidIndicesAllowed = $(this).attr('data-orchid-search');
            if($orchidIndicesAllowed && $orchidIndicesAllowed.length > 0) {
                $orchidIndicesAllowed = $orchidIndicesAllowed.split(",");
            }
            else {
                $orchidIndicesAllowed = null;
            }

            setSearchWorking(true);
            loadOrchidIndex($orchidIndicesAllowed, function () {
                searchOrchidIndex($query, function (results) {
                    displaySearchResults(results, function () {
                        setSearchWorking(false);
                    });
                });
            });
        });
    }

    function setSearchWorking(isWorking) {
        if (isWorking) {
            $('[data-orchid-search-progress]').show();
            $('[data-orchid-search-results]').hide();
        }
        else {
            $('[data-orchid-search-progress]').hide();
            $('[data-orchid-search-results]').show();
        }
        $(window).trigger('orchid.search.working');
    }

    function getOrchidDocuments($orchidIndicesAllowed, cb) {
        if (!window.orchidDocuments) {
            loadRootIndex($orchidIndicesAllowed, function (documents) {
                window.orchidDocuments = documents;
                window.orchidDocumentsMap = {};

                window.orchidDocuments.map(function (document) {
                    window.orchidDocumentsMap[document.link] = document;
                });

                cb(window.orchidDocuments);
            });
        }
        else {
            cb(window.orchidDocuments);
        }
    }

    function loadOrchidIndex($orchidIndicesAllowed, cb) {
        if (!window.orchidIdx) {
            getOrchidDocuments($orchidIndicesAllowed, function (docs) {
                window.orchidIdx = lunr(function () {
                    this.ref('link');
                    this.field('title');
                    this.field('description');
                    this.field('content');
                    this.metadataWhitelist = ['position'];

                    docs.forEach(function (doc) {
                        this.add(doc)
                    }, this)
                });

                cb(window.orchidIdx);
            });
        }
        else {
            cb(window.orchidIdx);
        }
    }

    function searchOrchidIndex(query, cb) {
        setTimeout(function () {
            var results = window.orchidIdx.search(query);
            cb(results);
        }, 1000)
    }

    function displaySearchResults(results, cb) {
        // console.log("displaying results");
        // console.log(results);

        var items = [];

        results.map(function (result) {
            var document = window.orchidDocumentsMap[result.ref];
            var summary = getSearchSummary(result, document);

            var $item = "<li><a href='" + document.link + "'>";
            $item += (items.length + 1) + ": " + document.title;
            $item += "</a><br>";
            if (summary.length > 0) {
                $item += "<p>" + summary + "</p>";
            }
            $item += "</li>";

            items.push($item);
        });

        var $searchResults = $('[data-orchid-search-results] ul');

        $searchResults.empty();
        $searchResults.html(items.join(''));

        cb();
    }

    function getSearchSummary(result, document) {
        var matches = 0;

        var startContextLength = 12;
        var endContextLength = 12;

        var snippets = [];

        // for every matching word
        for (var word in result.matchData.metadata) {

            // for every document field which has that word
            for (var field in result.matchData.metadata[word]) {
                if (field && result.matchData.metadata[word][field].position) {
                    matches += result.matchData.metadata[word][field].position.length;

                    for(var i_pos = 0; i_pos < result.matchData.metadata[word][field].position.length; i_pos++) {
                        if(i_pos === 3) break;

                        var snippetBounds = result.matchData.metadata[word][field].position[i_pos];
                        var pos_start = snippetBounds[0];
                        var pos_end = snippetBounds[0] + snippetBounds[1] + 1;

                        var match_start = snippetBounds[0];
                        var match_end = snippetBounds[0] + snippetBounds[1] + 1;

                        var leadingEllipses = false;
                        var trailingEllipses = false;

                        // add context to the beginning
                        if(pos_start > startContextLength) {
                            pos_start -= startContextLength;
                            leadingEllipses = true;
                        }
                        else {
                            pos_start = 0;
                        }

                        // add context to the end
                        if(pos_end < document[field].length - endContextLength) {
                            pos_end += endContextLength;
                            trailingEllipses = true;
                        }
                        else {
                            pos_end = document[field].length;
                        }

                        var snippet =
                            document[field].substring(pos_start, match_start) +
                            "<b>" + document[field].substring(match_start, match_end) + "</b>" +
                            document[field].substring(match_end, pos_end)
                        ;
                        snippet = snippet.trim();
                        if(leadingEllipses) {
                            snippet = "..." + snippet;
                        }
                        if(trailingEllipses) {
                            snippet = snippet + "...";
                        }
                        snippets.push(snippet);
                    }
                }
            }
        }

        return "<b><i>" + matches + " matches:</i></b><br>" + snippets.join("<br>");
    }

// Load indices via AJAX and build a map of documents that can be passed to Lunr
//----------------------------------------------------------------------------------------------------------------------

    function loadRootIndex($orchidIndicesAllowed, done) {
        var allDocs = [];

        var baseUrl = (window.site.baseUrl === '/') ? '' : window.site.baseUrl;

        $.getJSON(baseUrl + "/meta/index.json", function (data) {
            var childIndices = data.childrenPages.meta.ownPages;
            var childIndicesFinishedCount = 0;

            childIndices.map(function (indexPage) {
                var shouldLoad = true;
                if($orchidIndicesAllowed) {
                    var isIndexAllowed = false;
                    for(var i = 0; i < $orchidIndicesAllowed.length; i++) {
                        if(indexPage.reference.fileName === $orchidIndicesAllowed[i].trim() + ".index") {
                            isIndexAllowed = true;
                            break;
                        }
                    }
                    shouldLoad = isIndexAllowed;
                }

                if (shouldLoad) {
                    loadIndexPage(indexPage.reference.link,
                        function (document) {
                            allDocs.push(document);
                        },
                        function () {
                            childIndicesFinishedCount++;

                            if (childIndicesFinishedCount === childIndices.length) {
                                done(allDocs);
                            }
                        });
                }
                else {
                    childIndicesFinishedCount++;

                    if (childIndicesFinishedCount === childIndices.length) {
                        done(allDocs);
                    }
                }
            });
        });
    }

    function loadIndexPage(url, cb, done) {
        $.getJSON(url, function (data) {
            getChildDocuments(data, cb);
            done();
        });
    }

    function getChildDocuments(data, cb) {
        for (var key in data.childrenPages) {
            if (data.childrenPages.hasOwnProperty(key)) {
                var doc = data.childrenPages[key];

                if (doc.childrenPages) {
                    getChildDocuments(doc, cb);
                }
                else if (doc.ownPages) {
                    doc.ownPages.map(function (ownPage) {
                        ownPage.link = ownPage.reference.link;
                        ownPage.content = stripTags(ownPage.content);
                        delete ownPage.reference;
                        cb(ownPage);
                    });
                }
            }
        }
    }

    function stripTags(html) {
        var doc = new DOMParser().parseFromString(html, 'text/html');
        return doc.body.textContent || "";
    }

// Initialize searching
//----------------------------------------------------------------------------------------------------------------------

    $(function () {
        initializeSearchField();
    });

})(jQuery, lunr);