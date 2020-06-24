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
        var ajax = new $ax(Feng.ctxPath + "/mgr/coinExchange", function (data) {
            if(data.success){
            	Feng.success("兑换成功！");
            	 //传给上个页面，刷新table用
                admin.putTempData('formOk', true);
                //关掉对话框
                admin.closeThisDialog();
            }else{
            	  Feng.error("兑换失败！" + data.message);
            }
           
        }, function (data) {
            Feng.error("兑换失败！" + data.responseJSON.message);
        });
        ajax.set(data.field);
        ajax.start();
    });
});