layui.use(['form', 'upload', 'element', 'ax', 'laydate'], function () {
    var $ = layui.jquery;
    var form = layui.form;
    var upload = layui.upload;
    var element = layui.element;
    var $ax = layui.ax;
    var laydate = layui.laydate;

    //表单提交事件
    form.on('submit(moneyPasswordSettingForm)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/moneyPasswordSettingMgr/update", function (data) {
          if(data.success){
        		layer.msg('修改成功!',{icon:1,time:2000},function(){
        			window.location.reload();
        		});	
          }else{
        	  layer.msg( data.message ,{icon:2,time:2000},function(){
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