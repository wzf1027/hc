var host = 'http://192.168.101.123:8989/app';
layui.use(['form', 'element'], function () {
	var $ = layui.jquery;
	var viteCode = GetQueryString().code;
	//邀请码
	if(viteCode){
		$('#registervite').val(viteCode);
		$('#registervite').attr('disabled',true);
	}
	ajaxcaptcha();


	var androidUrl = $('#androidUrl').val();
	var iosUrl =  $('#iosUrl').val();
	var browser = {
		versions: function () {
			var u = navigator.userAgent, app = navigator.appVersion;
			return {//移动终端浏览器版本信息
				trident: u.indexOf('Trident') > -1, //IE内核
				presto: u.indexOf('Presto') > -1, //opera内核
				webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
				gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
				mobile: !!u.match(/AppleWebKit.*Mobile/i) || !!u.match(/MIDP|SymbianOS|NOKIA|SAMSUNG|LG|NEC|TCL|Alcatel|BIRD|DBTEL|Dopod|PHILIPS|HAIER|LENOVO|MOT-|Nokia|SonyEricsson|SIE-|Amoi|ZTE/), //是否为移动终端
				ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
				android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
				iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
				iPad: u.indexOf('iPad') > -1, //是否iPad
				webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
			};
		} (),
		language: (navigator.browserLanguage || navigator.language).toLowerCase()
	}
	$('#captchaimg').on('click',function(){
		ajaxcaptcha();
	});

//	注册
	$('#registerBtn').on('click',function(){
		if(!$('#registerTel').val()){
			layer.msg('请输入用户名！');
			return;
		}
		var reg1 = /^[a-zA-Z0-9]{6,10}$/;
		if(!reg1.test($('#registerTel').val())){
			layer.msg('输入的用户名要6到10位字母数字!');
			return ;
		}
		var reg = /^[a-zA-Z0-9]{6,16}$/;
 		if(!reg.test($('#registerPwd').val())){
 			layer.msg('输入的密码长度要6到16位!');
 			return ;
 		}
		if($('#registerPwd').val() != $('#registerrePwd').val()){
			layer.msg('两次密码输入不一致!');
			return;
		}
		 
		if(!$('#getCode').val()){
			layer.msg('请输入验证码！');
			return;
		}
		registerAjax(browser,androidUrl,iosUrl);
	})
})

function ajaxcaptcha() {
	$.ajax({
		type: "POST",
		url: host + "/captcha/code",
		dataType: "json",
		success: function (result) {
			if (result.code ==200){
				$("#captchaimg").prop('src', 'data:image/jpeg;base64,' + result.data.img);
				localStorage.setItem("ctoken", result.data.cToken);
			}
		}
	});
}

//注册
function registerAjax(browser,androidUrl,iosUrl){
	var data = {
		account:$('#registerTel').val(),
		password:$('#registerPwd').val(),
		againPassword:$('#registerrePwd').val(),
		code : $('#getCode').val(),
		ckToken:localStorage.getItem("ctoken"),
		recommendCode:$('#registervite').val()
	};
	$('#registerBtn').attr('disabled',true);
	$.ajax({
		type:"post",
		url:host+'/register',
		async:true,
		data:data,
		dataType:'json',
		success:function(data){
			if(data.code == '200'){
				layer.msg(data.message);
				$('#registerBtn').attr('disabled',true);
				if (browser.versions.iPhone || browser.versions.iPad || browser.versions.ios) {
					window.location.href = iosUrl;
				}
				if (browser.versions.android) {
					window.location.href = androidUrl;
				}
			}else{
				$('#registerBtn').attr('disabled',false);
				layer.msg(data.message);
			}
		}
	});
}
function GetQueryString(){
	var url = window.location.search; //获取url中"?"符后的字串dk
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
	    var str = url.substr(1);
	    strs = str.split("&");
	    for(var i = 0; i < strs.length; i ++) {
	    	theRequest[strs[i].split("=")[0]]=decodeURI(strs[i].split("=")[1]);     
	    }
  	}
  	return theRequest;
}