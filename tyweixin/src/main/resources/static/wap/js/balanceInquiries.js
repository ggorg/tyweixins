(function() {
    FastClick.attach(document.body);

    //切换tab
    $('.tabs>a').on('click', function() {
        var type = $(this).attr('data-type');
        var thisList =  $('.pd-tab[data-type="' + type + '"]');
        $('.tabs>a').removeClass('active');
        $(this).addClass('active');
        $('.pd-tab').hide();
        thisList.show();

        if(thisList.find('.detail-list li').length == 0) {
            loadRecordList();
        }


    });

    function loadRecordList() {
        var temp = '';
        for (var i = 0; i < 30; i++) {
            temp += '<li><div class="detail-con">' +
                '<span class="name">天翼电子商务有限公司-00000871205020</span>' +
                '<span>时间：2017-11-21  11:30:20</span>' +
                '<span>说明：充值</span>' +
                '<span class="money">30.00元</span>' +
                '</div></li>';
        }
        $('.detail-list').html(temp);
    }

})();