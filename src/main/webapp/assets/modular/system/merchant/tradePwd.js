layui.use(['form', 'upload', 'element', 'ax', 'laydate'], function () {
    var $ = layui.jquery;
    var form = layui.form;
    var upload = layui.upload;
    var element = layui.element;
    var $ax = layui.ax;
    var laydate = layui.laydate;

    $('.getCodeC').click(function(){
		sendInform(null);
	})
    
	function sendInform(data){
		$.ajax({
			type:"post",
			url:Feng.ctxPath +"/system/getMsg",
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
					if(data.message){
						layer.msg(data.message);
					}else{
						layer.msg('发送失败,请重新点击获取验证码');
					}
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
        newPassword: function (value) {
            if(!/\S/.test(value)){
                $("input[name='tradePwd']").focus();
                return '密码不能为空';
            }
            var reg = /^[a-zA-Z0-9]{6,10}$/;
    		if(!reg.test(value)){
    			return '输入的密码长度要6到16位';
    		} 
        },
        verifyCode: function (value) {
            if(!/\S/.test(value)){
                $("input[name='code']").focus();
                return '验证码不能为空';
            }
        }
    });
    //表单提交事件
    form.on('submit(merchantTradeForm)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/merchantMgr/updateTradepwd", function (data) {
          if(data.success){
        		layer.msg('设置成功!',{icon:1,time:3000},function(){
        			window.location.reload();
        		});	
          }else{
        	  layer.msg( data.message ,{icon:1,time:3000},function(){
      			window.location.reload();
      		});
          }
        }, function (data) {
            Feng.error("设置失败!" + data.responseJSON.message + "!");
        });
        ajax.set(data.field);
        ajax.start();
    });
    
});