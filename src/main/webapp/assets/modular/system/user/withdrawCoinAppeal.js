layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();
    
    // 添加表单验证方法
/*    form.verify({
        psw: [/^[\S]{6}$/, '密码必须6，且不能出现空格'],
        repsw: function (value) {
            if (value !== $('#userForm input[name=password]').val()) {
                return '两次密码输入不一致';
            }
        }
    });
*/


    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/userWithdrawCoinAppealMgr/add", function (data) {
            if(data.success){
            	Feng.success("添加成功！");
            	 //传给上个页面，刷新table用
                admin.putTempData('formOk', true);
                //关掉对话框
                admin.closeThisDialog();
            }else{
            	  Feng.error("添加失败！" + data.message);
            }
           
        }, function (data) {
            Feng.error("添加失败！" + data.responseJSON.message);
        });
        ajax.set(data.field);
        ajax.start();
    });
});