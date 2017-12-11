(function() {
    FastClick.attach(document.body);
    var interval = null;

    // 获取注册短信验证码
    $('.sign-up').on('click', '.code-sms', function(e) {
    	getCode(e);
    });

    // 绑定
    $('.inup-content').on('click', '#btn_binding', function(e) {
    	signUp(e);
    });

    // 倒计时， 获取短信验证码
    function getCode(e) {
    	var tel = $('#tel').val().trim()
    	if(tel.length <= 0) {
    		Util.toast('请输入手机号码');
    		return
    	}
    	if(tel.match(/^(133|153|177|180|181|189).*$/)==null){
            Util.toast('抱歉，非电信手机号码不能绑定');
            return
        }


    	var displayEl = $(e.currentTarget);
        var time = 60;
        // Util.getCodeCountdown(60, displayEl);
        if (displayEl.hasClass('counting')) {
            return;
        }
        displayEl.html(time + 's');
        displayEl.addClass('counting');
        if (interval) {
            clearInterval(interval);
        }

        interval = Util.countdownFun(time, function(t) {
            displayEl.html(t + 's');
        }, function(t) {
            displayEl.removeClass('counting');
            displayEl.html('获取验证码');
        });
        Util.Ajax({
            url:"do-send-vaild-code",
            type:"post",
            dataType:"JSON",
            data:{telphone:tel,openid:"test"},
            cbOk:function(data){
                if(data.reCode==-2){
                    Util.toast(data.reMsg);
                    displayEl.removeClass('counting');
                    displayEl.html('获取验证码');
                    clearInterval(interval);
                }else if(data.reCode==-1){
                    Util.toast(data.reMsg);
                }
            }
        })
    }


    // 验证手机号码
    function valiPhone(tel) {
    	if(!(/^1[0-9]{10}$/.test(tel))){ 
        	return false; 
    	}
    	return true;
    }
    
    // 注册
    function signUp(e) {
    	var tel = $('#tel').val().trim(),
    		code = $('#code').val().trim();
    	if(tel.length <= 0) {
    		Util.toast('手机号码不能为空');
    		return;
    	}
    	if(code.length <= 0) {
    		Util.toast('验证码不能为空');
    		return;
    	}

    	if(!valiPhone(tel)) {
    		Util.toast('请输入正确的手机号码');
    		return;
    	}

    	// todo 发送ajax请求绑定
        Util.Ajax({
            url:"do-bind",
            type:"post",
            dataType:"JSON",
            data:{telphone:tel,openid:"test",code:code},
            cbOk:function(data){
                if(data.reCode!=1){
                    Util.toast(data.reMsg);

                }else {
                    Util.iconToast('<i class="icon-success"></i>绑定成功');
                    window.setTimeout(tyCloseWin(),1500)
                }
            }
        })

    }


    //Util.iconToast('<i class="icon-warn"></i>您还不是翼支付用户<br/>请下载翼支付注册');

})();