layui.use(['layer', 'form','ax','element'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var element = layui.element;
        var $ax = layui.ax;
        var form = layui.form;

        $('.getCodeC').click(function(){
			if(!$('#phone').val()){
				layer.msg('请输入手机号码！');
				return;
			}
			var data = {
				phone:$('#phone').val()
			};
			sendInform(data);
		})
        
		function sendInform(data){
			$.ajax({
				type:"post",
				url:Feng.ctxPath +"/app/sms/getMsg",
				data:data,
				dataType:'json',
				success:function(data){
					if(data.code=='200'){
						var timerY = null, tn = 120;
						$('#getBtn').attr('disabled',true);
						timerY = setInterval(function(){
							$('#getBtn').val('重发验证码('+ tn +'s)');
							tn --;
							if(tn < 1){
								$('#getBtn').val('重发验证码');
								$('#getBtn').attr('disabled',false);
								clearInterval(timerY);
							}
						},1000);
					}else{
						layer.msg('发送失败,请重新点击获取验证码');
						$('#getBtn').attr('disabled',false);
					}
				},
				error:function(xhr,type,errorThrown){
					layer.msg('发送失败,请重新点击获取验证码');
					$('#getBtn').attr('disabled',false);
				}
			});
		}
        // 验证
        form.verify({
            phone: function (value) {
                if(!/\S/.test(value)){
                    $("input[name='phone']").focus();
                    return '手机号码不能为空';
                }
                if(!/^1[34578]\d{9}$/.test(value)){
                    $("input[name='phone']").focus();
                    return '电话号码格式有误';
                }
            },
            newPassword: function (value) {
                if(!/\S/.test(value)){
                    $("input[name='password']").focus();
                    return '密码不能为空';
                }
                var reg = /^[a-zA-Z0-9]{6,10}$/;
        		if(!reg.test(value)){
        			return '输入的密码长度要6到16位';
        		} 
            },
            rePassword: function (value) {
                if(!/\S/.test(value)){
                    $("input[name='rePassword']").focus();
                    return '确认密码不能为空';
                }
              var pwd =  $("input[name='password']").val();
              if(pwd != value){
            	  return '两次密码输入不一致!';
              }
            },
            verifyCode: function (value) {
                if(!/\S/.test(value)){
                    $("input[name='code']").focus();
                    return '验证码不能为空';
                }
            },
            refenceCode:function (value) {
                if(!/\S/.test(value)){
                    $("input[name='refenceCode']").focus();
                    return '邀请码不能为空';
                }
            }
        });
        
        /**
         * 去登录页面
         */
        $('#accepGo').click(function(){
        	window.location.href="/login";
		});
        
        // 表单提交事件
        form.on('submit(btnSubmit)', function (data) {
            var ajax = new $ax(Feng.ctxPath + "/userRegister", function (data) {
            	if(data.code ==200){
            		layer.msg('注册成功',{icon:1,time:3000},function(){
            			window.location.href="/login";
            		});	
            	}else{
            		layer.msg(data.message);
            	}
            }, function (data) {
            	layer.msg("注册失败！" + data.responseJSON.message);
            });
            ajax.set(data.field);
            ajax.start();
            return false;
        });
});