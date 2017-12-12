(function() {
    FastClick.attach(document.body);

    var _body  = $('body');

    _body.on('click', '#showRule', function () {
        aniShow("#ruleMask")
    })

    _body.on('click', '#clsoeRuleMask', function () {
        aniHide("#ruleMask")
    })


    function aniShow(showMask) {
        var showMask = $(showMask);
        showMask.removeClass('hide');
        showMask.find('.masker').removeClass('aniFadeOut').addClass('aniFadeIn');
        showMask.find('.pop-container').removeClass('aniHide').addClass('aniShow');
        Util.disableScroll();
    }

    function aniHide(hideMask) {
        var hideMask = $(hideMask);
        hideMask.find('.masker').removeClass('aniFadeIn').addClass('aniFadeOut');
        hideMask.find('.pop-container').removeClass('aniShow').addClass('aniHide');
        setTimeout(function() {
            hideMask.addClass('hide');
        }, 400);
        Util.enableScroll();
    }


})();