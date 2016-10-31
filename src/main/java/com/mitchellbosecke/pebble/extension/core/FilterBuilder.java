package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.extension.Filter;

import java.util.HashMap;
import java.util.Map;

public class FilterBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean abbreviate = true;
    private boolean abs = true;
    private boolean capitalize = true;
    private boolean date = true;
    private boolean defaultValue = true;
    private boolean first = true;
    private boolean join = true;
    private boolean last = true;
    private boolean lower = true;
    private boolean numberformat = true;
    private boolean slice = true;
    private boolean sort = true;
    private boolean rsort = true;
    private boolean title = true;
    private boolean trim = true;
    private boolean upper = true;
    private boolean urlencode = true;
    private boolean length = true;
    private boolean replace = true;
    private boolean merge = true;

    private String abbreviateName = "abbreviate";
    private String absName = "abs";
    private String capitalizeName = "capitalize";
    private String dateName = "date";
    private String defaultValueName = "default";
    private String firstName = "first";
    private String joinName = "join";
    private String lastName = "last";
    private String lowerName = "lower";
    private String numberformatName = "numberformat";
    private String sliceName = "slice";
    private String sortName = "sort";
    private String rsortName = "rsort";
    private String titleName = "title";
    private String trimName = "trim";
    private String upperName = "upper";
    private String urlencodeName = "urlencode";
    private String lengthName = "length";
    private String replaceName = ReplaceFilter.FILTER_NAME;
    private String mergeName = MergeFilter.FILTER_NAME;

    public FilterBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public FilterBuilder disableAll() {
        abbreviate = false;
        abs = false;
        capitalize = false;
        date = false;
        defaultValue = false;
        first = false;
        join = false;
        last = false;
        lower = false;
        numberformat = false;
        slice = false;
        sort = false;
        rsort = false;
        title = false;
        trim = false;
        upper = false;
        urlencode = false;
        length = false;
        replace = false;
        merge = false;

        return this;
    }

    public FilterBuilder enableAbbreviate() {
        abbreviate = true;
        return this;
    }

    public FilterBuilder enableAbs() {
        abs = true;
        return this;
    }

    public FilterBuilder enableCapitalize() {
        capitalize = true;
        return this;
    }

    public FilterBuilder enableDate() {
        date = true;
        return this;
    }

    public FilterBuilder enableDefaultValue() {
        defaultValue = true;
        return this;
    }

    public FilterBuilder enableFirst() {
        first = true;
        return this;
    }

    public FilterBuilder enableJoin() {
        join = true;
        return this;
    }

    public FilterBuilder enableLast() {
        last = true;
        return this;
    }

    public FilterBuilder enableLower() {
        lower = true;
        return this;
    }

    public FilterBuilder enableNumberformat() {
        numberformat = true;
        return this;
    }

    public FilterBuilder enableSlice() {
        slice = true;
        return this;
    }

    public FilterBuilder enableSort() {
        sort = true;
        return this;
    }

    public FilterBuilder enableRsort() {
        rsort = true;
        return this;
    }

    public FilterBuilder enableTitle() {
        title = true;
        return this;
    }

    public FilterBuilder enableTrim() {
        trim = true;
        return this;
    }

    public FilterBuilder enableUpper() {
        upper = true;
        return this;
    }

    public FilterBuilder enableUrlencode() {
        urlencode = true;
        return this;
    }

    public FilterBuilder enableLength() {
        length = true;
        return this;
    }

    public FilterBuilder enableReplace() {
        replace = true;
        return this;
    }

    public FilterBuilder enableMerge() {
        merge = true;
        return this;
    }

    public FilterBuilder disableAbbreviate() {
        abbreviate = false;
        return this;
    }

    public FilterBuilder disableAbs() {
        abs = false;
        return this;
    }

    public FilterBuilder disableCapitalize() {
        capitalize = false;
        return this;
    }

    public FilterBuilder disableDate() {
        date = false;
        return this;
    }

    public FilterBuilder disableDefaultValue() {
        defaultValue = false;
        return this;
    }

    public FilterBuilder disableFirst() {
        first = false;
        return this;
    }

    public FilterBuilder disableJoin() {
        join = false;
        return this;
    }

    public FilterBuilder disableLast() {
        last = false;
        return this;
    }

    public FilterBuilder disableLower() {
        lower = false;
        return this;
    }

    public FilterBuilder disableNumberformat() {
        numberformat = false;
        return this;
    }

    public FilterBuilder disableSlice() {
        slice = false;
        return this;
    }

    public FilterBuilder disableSort() {
        sort = false;
        return this;
    }

    public FilterBuilder disableRsort() {
        rsort = false;
        return this;
    }

    public FilterBuilder disableTitle() {
        title = false;
        return this;
    }

    public FilterBuilder disableTrim() {
        trim = false;
        return this;
    }

    public FilterBuilder disableUpper() {
        upper = false;
        return this;
    }

    public FilterBuilder disableUrlencode() {
        urlencode = false;
        return this;
    }

    public FilterBuilder disableLength() {
        length = false;
        return this;
    }

    public FilterBuilder disableReplace() {
        replace = false;
        return this;
    }

    public FilterBuilder disableMerge() {
        merge = false;
        return this;
    }

    public FilterBuilder useAbbreviateName(String name) {
        abbreviateName = name;
        return this;
    }

    public FilterBuilder useAbsName(String name) {
        absName = name;
        return this;
    }

    public FilterBuilder useCapitalizeName(String name) {
        capitalizeName = name;
        return this;
    }

    public FilterBuilder useDateName(String name) {
        dateName = name;
        return this;
    }

    public FilterBuilder useDefaultValueName(String name) {
        defaultValueName = name;
        return this;
    }

    public FilterBuilder useFirstName(String name) {
        firstName = name;
        return this;
    }

    public FilterBuilder useJoinName(String name) {
        joinName = name;
        return this;
    }

    public FilterBuilder useLastName(String name) {
        lastName = name;
        return this;
    }

    public FilterBuilder useLowerName(String name) {
        lowerName = name;
        return this;
    }

    public FilterBuilder useNumberformatName(String name) {
        numberformatName = name;
        return this;
    }

    public FilterBuilder useSliceName(String name) {
        sliceName = name;
        return this;
    }

    public FilterBuilder useSortName(String name) {
        sortName = name;
        return this;
    }

    public FilterBuilder useRsortName(String name) {
        rsortName = name;
        return this;
    }

    public FilterBuilder useTitleName(String name) {
        titleName = name;
        return this;
    }

    public FilterBuilder useTrimName(String name) {
        trimName = name;
        return this;
    }

    public FilterBuilder useUpperName(String name) {
        upperName = name;
        return this;
    }

    public FilterBuilder useUrlencodeName(String name) {
        urlencodeName = name;
        return this;
    }

    public FilterBuilder useLengthName(String name) {
        lengthName = name;
        return this;
    }

    public FilterBuilder useReplaceName(String name) {
        replaceName = name;
        return this;
    }

    public FilterBuilder useMergeName(String name) {
        mergeName = name;
        return this;
    }



    public Map<String, Filter> build() {
        Map<String, Filter> filters = new HashMap<>();

        if(abbreviate) {
            filters.put(abbreviateName, new AbbreviateFilter());
        }
        if(abs) {
            filters.put(absName, new AbsFilter());
        }
        if(capitalize) {
            filters.put(capitalizeName, new CapitalizeFilter());
        }
        if(date) {
            filters.put(dateName, new DateFilter());
        }
        if(defaultValue) {
            filters.put(defaultValueName, new DefaultFilter());
        }
        if(first) {
            filters.put(firstName, new FirstFilter());
        }
        if(join) {
            filters.put(joinName, new JoinFilter());
        }
        if(last) {
            filters.put(lastName, new LastFilter());
        }
        if(lower) {
            filters.put(lowerName, new LowerFilter());
        }
        if(numberformat) {
            filters.put(numberformatName, new NumberFormatFilter());
        }
        if(slice) {
            filters.put(sliceName, new SliceFilter());
        }
        if(sort) {
            filters.put(sortName, new SortFilter());
        }
        if(rsort) {
            filters.put(rsortName, new RsortFilter());
        }
        if(title) {
            filters.put(titleName, new TitleFilter());
        }
        if(trim) {
            filters.put(trimName, new TrimFilter());
        }
        if(upper) {
            filters.put(upperName, new UpperFilter());
        }
        if(urlencode) {
            filters.put(urlencodeName, new UrlEncoderFilter());
        }
        if(length) {
            filters.put(lengthName, new LengthFilter());
        }
        if(replace) {
            filters.put(replaceName, new ReplaceFilter());
        }
        if(merge) {
            filters.put(mergeName, new MergeFilter());
        }

        return filters;
    }

}
