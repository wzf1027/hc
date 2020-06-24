layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();


    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/updateNumber", function (data) {
        	if(data.success){
        		  Feng.success("补单成功！");
        	}else{
        		Feng.error("补单失败！" + data.message);
        	}
          
            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);
            //关掉对话框
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("补单失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
});