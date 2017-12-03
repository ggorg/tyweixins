(function(global) {
    (function() {
        var pendingRequests = {};

        function generatePendingRequestKey(options) {
            var key = options.url + '?' + options.data;
            return key;
        }

        function storePendingRequest(key, xhr) {
            xhr.pendingRequestKey = key;
            pendingRequests[key] = xhr;
        }

        $.ajaxPrefilter(function(options, originalOptions, jqXHR) {
            console.log("----ajaxPrefilter-----")

            // 不重复发送相同请求
            var key = generatePendingRequestKey(options);
            if (!pendingRequests[key]) {
                storePendingRequest(key, jqXHR);
            } else {
                // or do other
                jqXHR.abort();
            }

            var complete = options.complete;
            options.complete = function(jqXHR, textStatus) {
                // clear from pending requests
                pendingRequests[jqXHR.pendingRequestKey] = null;
                if ($.isFunction(complete)) {
                    complete.apply(this, arguments);
                }
            };
        });
    })()
    /* ==================弹出框==dialog================= */
    /*
                options : {
                    title: 提示标题,
                    content: 提示内容(可传入模版）,
                    btns: [{
                        name: 按钮名称
                        cb: callback
                    }] // 若按钮数组只有一个，那么是alert，按钮数组两个，第一个为negative按钮，第二个为positive按钮
                }
            */
    function popup(options) {
        var _options = {
            "title": "",
            "content": "",
            "btns": [{
                "name": "",
                "cb": function() {}
            }, {
                "name": "",
                "cb": function() {}
            }]
        };
        _options = $.extend(_options, options || {});
        var dialog_component = $("<div/>").addClass('popup').addClass("component-dialog"),
            masker = $("<div/>").addClass("masker"),
            dialog_wrapper = $("<div/>").addClass("dialog-wrapper"),
            dialog_title = _options.title ? $("<h5/>").addClass('with-border').html(_options.title) : '',
            dialog_content = $("<div/>").addClass("dialog-content").html(_options.content),
            btns = $("<div/>").addClass("btns"),
            negative_btn = '',
            positive_btn = '',
            ne_cb = null,
            po_cb = null;
        dialog_wrapper.append(dialog_title);
        dialog_wrapper.append(dialog_content);
        if (_options.btns.length > 1) {
            negative_btn = $("<div/>").addClass("btn").addClass("btn-nav").html(_options.btns[0].name);
            positive_btn = $("<div/>").addClass("btn").addClass("btn-pos").html(_options.btns[1].name);
            btns.append(negative_btn).append(positive_btn);
            ne_cb = _options.btns[0].cb;
            po_cb = _options.btns[1].cb;
            dialog_wrapper.append(btns);
        } else {
            positive_btn = $("<div/>").addClass("btn").addClass("btn-pos").html(_options.btns[0].name);
            dialog_wrapper.append(positive_btn);
            po_cb = _options.btns[0].cb;
        }

        // dialog_wrapper.append(dialog_content).append(btns);

        dialog_component.append(masker).append(dialog_wrapper);

        $("body").append(dialog_component);
        // disableScrolling();
        Util.disableScroll();
        //事件绑定
        masker.bind("click", function(e) {
            hide(dialog_component);
        });

        negative_btn && negative_btn.bind("click", function(e) {
            ne_cb && ne_cb.call(this, arguments);
            hide(dialog_component);
        });

        positive_btn.bind("click", function(e) {
            po_cb && po_cb.call(this, arguments);
            hide(dialog_component);
        });

        function hide(target) {
            target.remove();
            // enableScrolling();
            Util.enableScroll();
        }
    }

    // 测试地址

    // 发送弹幕消息扣除团票
    var Util = {
        getParam: function(name, url) {
            if (!url) {
                url = location.href;
            }
            var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
            var returnValue;
            for (var i = 0; i < paraString.length; i++) {
                var tempParas = paraString[i].split('=')[0];
                var parasValue = paraString[i].split('=')[1];
                if (tempParas === name)
                    returnValue = parasValue;
            }

            if (!returnValue) {
                return "";
            } else {
                if (returnValue.indexOf("#") != -1) {
                    returnValue = returnValue.split("#")[0];
                }
                return returnValue;
            }
        },
        toast: function(msg, duration) {
            duration = duration || 1500;
            var _toast = $("<div/>").addClass('toast').html(msg);
            $('body').append(_toast);
            setTimeout(function() {
                _toast.remove();
            }, duration);
        },
        iconToast: function(msg, duration) {
            duration = duration || 2000;
            var _iconToast = $("<div/>").addClass('iconToast').html(msg);
            $('body').append(_iconToast);
            setTimeout(function() {
                _iconToast.remove();
            }, duration);
        },
        showLoader: function(msg) {
            if ($('body').find('.component-loading').length <= 0) {
                $('body').append('<div class="component-loading"><div class="loading-wrapper"><i class="icon-loader"></i><span>' + (msg || '加载中...') + '</span></div></div>');
            }
        },
        hideLoader: function() {
            $('body').find('.component-loading').remove();
        },
        setSessionStorage: function(key, obj) {
            obj = obj || {};
            if (window.sessionStorage) {
                window.sessionStorage[key] = JSON.stringify(obj)
            } else {
                window.mySessionStorage[key] = JSON.stringify(obj)

            }
        },
        getSessionStorage: function(key) {
            var objStr;
            if (window.sessionStorage) {
                objStr = window.sessionStorage[key];
            } else {
                objStr = window.mySessionStorage[key];
            }
            return objStr == null ? null : JSON.parse(objStr);
        },
        isIosPlatform: function() {
            if (navigator.userAgent.match(/(iPad|iPhone)/)) {
                return true;
            } else {
                return false;
            }
        },
        isAndroidPlatform: function() {
            if (navigator.userAgent.match(/(Android)/)) {
                return true;
            } else {
                return false;
            }
        },
        isWeiXin: function() {
            return (/micromessenger/i).test(navigator.userAgent.toLowerCase());
        },
        isApp: function() {
            return (/sulaidaiapp/i).test(navigator.userAgent.toLowerCase());
        },
        /* ==================禁止滚动======================== */
        disableScroll: function() {
            $(".scroll").removeClass("scroll-active");
        },
        enableScroll: function() {
            $(".scroll").addClass("scroll-active");
        },
        popup: popup,
        /** 显示结果
         * options {
         *   result: 0 -- 失败， 1-- 成功,
         *   status: 状态描述
         *   info: 补充描述，
         *   btn: {
         *          name: 按钮名称，
                    cb: function () {} 回调
         *   }
         * }
         */
        showResult: function(options) {
            var _options = {
                'result': 1,
                'status': '',
                'info': '',
                'btn': {
                    'name': '返回',
                    'cb': null
                }
            };
            _options = $.extend(_options, options || {});
            var $resultwrapper = $('<div/>').addClass('popup').addClass('pop-result'),
                $result = $('<div/>').addClass('result').addClass(_options.result == '1' ? 'success' : 'fail'),
                $status = $('<p/>').html(_options.status),
                $info = $('<span/>').addClass('tip').html(_options.info),
                $btn = $('<div/>').addClass('btn').addClass('btn-full').html(_options.btn.name);
            $resultwrapper.append($result).append($status).append($info).append($btn);
            $("body").append($resultwrapper);
            Util.disableScroll();
            $btn.bind("click", function(e) {
                _options.btn.cb && _options.btn.cb.call(this, arguments);
                // hide($resultwrapper);
            });

            // function hide(target) {
            //     target.remove();
            //     // enableScrolling();
            //     Util.enableScroll();
            // }
        },
        // options {
        //     header: id;
        //     leftFun: function () {},
        //     rightFun: function () {}
        // }
        initHeader: function(options) {
            var $header = $('#' + options.header);
            $header.on('click', '.btn-back', function(e) {
                if (options.leftFun) {
                    options.leftFun();
                } else {
                    window.history.back();
                }
            });

            $header.on('click', '.btn-right', function(e) {
                options.rightFun && options.rightFun();
            });
        },
        // footer init 其中 id 为footer 的id
        initFooter: function(id) {
            var $footer = $('#' + id);
            $footer.on('click', 'li', function(e) {
                var $target = $(e.currentTarget);
                $footer.find('li').removeClass('active');
                $target.addClass('active');
                window.location.href = $target.attr('data-href');
            })

        },
        /**
            options {
                selector: 父节点
                tips: 提示
                subTxt: 灰色字体提示
                clickEve: 无数据点击事件
            }
        */
        showNoData: function(options) {
            var _wraper = $(options.selector);
            var _noData = _wraper && _wraper.find('.no-data');
            if (_noData && _noData.length > 0) {
                _noData.show();
            } else {
                var tipsTxt = options.tips || '暂无相关数据',
                    subTxt = options.subTxt || '点击屏幕，重新加载';
                _noData = $('<div/>').addClass('no-data');
                var _ndContainer = $('<div/>').addClass('nd-container'),
                    _ndTips = $('<span/>').addClass('nd-tips').html(tipsTxt),
                    _subTxt = $('<span/>').addClass('nd-sub-txt').html(subTxt);
                _ndContainer.append(_ndTips).append(_subTxt);
                _noData.append(_ndContainer);
                _wraper.append(_noData);
                _wraper.on('click', function() {
                    if (options.clickEve) {
                        options.clickEve()
                    }else{
                        location.reload();
                    }
                });
            }
        },
        /* 
            config: {
                url: 请求路径,
                type: 请求类型，
                dataType: 返回的数据类型，
                cbOk: 成功回调，
                cbErr: 失败回调，
                cbCp: 完成回调
            }
        */
        Ajax: function(config) {
            var me = this;
            $.ajax({
                timeout: 20 * 1000,
                url: config.url,
                type: config.type,
                data: config.data,
                dataType: config.dataType,
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                beforeSend: function(xhr, settings) {
                    // xhr.setRequestHeader("If-Modified-Since", "0");
                    me.showLoader();
                    config.beforeSend && config.beforeSend(xhr, settings);

                },
                success: function(data, textStatus, jqXHR) {
                    config.cbOk && config.cbOk(data, textStatus, jqXHR);
                },
                error: function(e, xhr, type) {
                    config.cbErr && config.cbErr(e, xhr, type);
                },
                complete: function(xhr, status) {
                    me.hideLoader();
                    config.cbCp && config.cbCp(xhr, status);
                }
            });
        },
        // 倒计时
        countdownFun: function(time, cb, cbEnd) {
            var that = this;
            var interval = setInterval(function() {
                if (time <= 1) {
                    cbEnd(time);
                } else {
                    time -= 1;
                    cb(time);
                    // that.countdownFun(time, cb, cbEnd);
                }
            }, 1000);
            return interval;
        },
        // 验证手机号码
        valiPhone: function(tel) {
            if (!(/^1[0-9]{10}$/.test(tel))) {
                return false;
            }
            return true;
        },
        // 设计稿像素转换到实际像素
        pxTopx: function(px) {
            var _clientWidth = document.documentElement.clientWidth > 414 ? 414 : document.documentElement.clientWidth,
                _fontsize = 20 * (_clientWidth / 320);
            return px / 23.4375 / 2 * _fontsize;
        },
        addHandler: function(element, type, handler) {
            if (element.addEventListener) {
                element.addEventListener(type, handler, false);
            } else if (element.attachEvent) {
                element.attachEvent("on" + type, handler);
            } else {
                element["on" + type] = handler;
            }
        },
        //绑定 pageshow事件
        onPageShow: function(callback) {
            var showCount = 0;
            this.addHandler(window, "pageshow", function(event) {
                showCount++;
                if (callback) {
                    callback(showCount);
                }

            });
        },
        //去掉firefox和safari的往返缓存（bfcache）
        pageShowReload: function() {
            this.onPageShow(function(showcount) {
                if (showcount > 1) {
                    window.location.reload();
                }
            })
        }
    }

    global.Util = Util;
})(window);