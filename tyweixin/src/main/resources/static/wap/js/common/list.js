/*
 * List 
 *
 * 
 * 
 *
 */

(function() {
    var myScroll, _loading, wrapperHeight, _target,
        distance = 0, //触发刷新的滑动距离
        isLoading = false,
        _options = {
            hasMore: true,
            mod: 'next' //加载类型 'next'-下一页数据；'reload'-刷新数据
        },
        canRefresh = false;


    //type为0表示添加刷新的loading， 为1表示添加加载下一页的loading
    function addLoading(type) {
        var svgTemp = '<svg class="spinner show" viewBox="0 0 44 44"><circle class="path" fill="none" stroke-width="4" stroke-linecap="round" cx="22" cy="22" r="20"></circle> </svg>';
        _loading = $('<div/>').addClass('loading-container').html(svgTemp);
        if (type) {
            _loading.addClass('more-loading');
            _target.children().eq(0).append(_loading);
        } else {
            _target.children().eq(0).prepend(_loading);
        }

    }

    function List(el, options) {
        "use strict";
        _target = $(el);
        //计算容器高度
        // var _contentPadding = $('.content').css('padding').split('px');
        // wrapperHeight = $(window).height() - _target.offset().top - _contentPadding[2];
        // console.info('wrapperHeight-----', wrapperHeight);
        // _target.height(wrapperHeight);
        _options.hasMore && addLoading(1);
        _options = $.extend(_options, options);

        
        distance = $('.pull-refresh').height();
        //初始化iscroll
        myScroll = new IScroll(el, {
            probeType: 2,
            disableMouse: true,
            tap: true,
        })
        myScroll.on('scrollEnd', function() {
            console.info(this.directionY, this.y, this.maxScrollY);
            //加载下一页
            if (this.directionY === 1 && this.y === this.maxScrollY) {
                if (_options.hasMore) {
                    _target.find('.loading-container').eq(1).show();
                    !isLoading && typeof _options.loadMore === 'function' && _options.loadMore.call(this);
                } else {
                    _target.find('.loading-container').eq(1).hide();
                    myScroll.refresh();
                }
                // !isLoading && typeof _options.loadMore === 'function' && _options.loadMore.call(this);
            }

            if (canRefresh) {
                canRefresh = false;
                _target.find('.scroll-icon-arrow').removeClass('flip-up').addClass('flip-down');
                _target.find('.arrow-wrapper').hide();
                _target.find('.refresh-loading').show();
                !isLoading && typeof _options.reload === 'function' && _options.reload.call(this);
            }


            // if (this.y > -startDis && this.y < (-startDis / 2) && (this.directionY === -1 || this.directionY === 0)) {
            //     //下拉距离不足够触发刷新
            //     myScroll.scrollTo(0, -startDis, 500);

            // }

            // //刷新
            // console.info("this.directionY=" +this.directionY, "this.y=" + this.y);
            // if ((this.directionY === -1 || this.directionY === 0) && this.y >= (-startDis / 2)) {
            //     !isLoading && typeof _options.reload === 'function' && _options.reload.call(this);
            // }
            // myScroll.scrollTo(0, -startDis, 500);

        });
        myScroll.on('scroll', function() {
            // console.info('scroll---', 'this.directionY=' + this.directionY, 'this.y=' + this.y, 'this.maxScrollY=' + this.maxScrollY);
            if (this.y >= distance) {
                _target.addClass('refreshing');
                _target.find('.scroll-icon-arrow').removeClass('flip-down').addClass('flip-up');
                canRefresh = true;
            } else {
                _target.removeClass('refreshing');
                _target.find('.scroll-icon-arrow').removeClass('flip-up').addClass('flip-down');
                canRefresh = false;
            }
        });

    }


    List.prototype = {
        hasMore: _options.hasMore,
        /**
            mod: 请求数据类型 'next'-下一页数据；'reload'-刷新数据
        */
        isLoading: function(bool, mod) {
            isLoading = bool;
            _options.mod = mod;
        },
        //刷新列表
        refresh: function(type) {
            _options.hasMore = this.hasMore;
            if (_options.hasMore) {
                _target.find('.more-loading').show();
            } else {
                _target.find('.more-loading').hide();
            }
            // _options.mod === 'reload' && myScroll.scrollTo(0, -startDis, 500);
            _target.removeClass('refreshing');
            _target.find('.scroll-icon-arrow').removeClass('flip-up').addClass('flip-down');
            _target.find('.arrow-wrapper').show();
            _target.find('.refresh-loading').hide();
            canRefresh = false;
            myScroll.refresh();
        }
    }

    if (typeof module != 'undefined' && module.exports) {
        module.exports = List;
    } else if (typeof define == 'function' && define.amd) {
        define(function() {
            return List;
        });
    } else {
        window.List = List;
    }
})();