(function ($) {
    $(function () {
        var $window = $(window);
        $window.on('orchid.search.working', function () {
            $window.trigger('resize.sidebar-lock');
        });
    });
})(jQuery);