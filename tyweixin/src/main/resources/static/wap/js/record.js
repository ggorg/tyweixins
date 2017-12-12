(function() {
    FastClick.attach(document.body);

    var recordList,
        pageData = {
            curPage: 1,
            pageSize: 10
        };

    //计算列表容器高度
    var _contentPadding = $('.content').css('padding').split('px');
    var wrapperHeight = $(window).height() - $('.list-container').offset().top - _contentPadding[2];
    $('.list-container').height(wrapperHeight);

    function loadData() {
        //模拟ajax请求获取数据
        var mod = pageData.curPage === 1 ? 'reload' : 'next'; //正在请求刷新数据还是下一页数据
        recordList && recordList.isLoading(true, mod); //请求时将list的loading状态设置为true
        setTimeout(function() {
            recordList && recordList.isLoading(false, mod); //请求完成将list的loading状态设置为false
            /*
             ajax请求成功
             更新curPage的值
             判断是否是最后一页，是的话设置list的hasMore为false  recordList && recordList.hasMore = false;
             */
            //todo

            var temp = '';
            for (var i = 0; i < 10; i++) {
                var recordItem = {
                    id: pageData.curPage * pageData.pageSize + i,
                    name: '翼支付红包',
                    time: '2017.12.05  14:26:30',
                    money: '30.00'
                };
                temp += '<li class="record-tr" data-id="' + recordItem.id + '" >' +
                    '<span class="name">' + recordItem.name + '</span>' +
                    '<span class="time">' + recordItem.time + '</span>' +
                    '<span class="money">+' + recordItem.money + '</span>' +
                    '</li>';
            }

            if (pageData.curPage == 1) {
                $('.record-list').html(temp);
            } else {
                $('.record-list').append(temp);
            }

            if (recordList) {
                recordList.refresh();
            } else {
                recordList = new List('#recordList', {
                    loadMore: function() {
                        console.info('loadMore-------');
                        pageData.curPage += 1;
                        loadData();
                    },
                    reload: function() {
                        console.info('reload-------');
                        pageData.curPage = 1;
                        loadData();
                    }
                });

            }

        }, 1000);
    }
    loadData();

})();