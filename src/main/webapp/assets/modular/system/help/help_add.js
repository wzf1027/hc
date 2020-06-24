layui.use(['layer', 'form', 'admin', 'ax','upload','layedit'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var upload = layui.upload;
    var admin = layui.admin;
    var layer = layui.layer;
    var layedit = layui.layedit;
    // 让当前iframe弹层高度适应
    admin.iframeAuto();
  //上传图片,必须放在 创建一个编辑器前面
	layedit.set({
        uploadImage: {
            url: Feng.ctxPath +'/image/uploadImg', //接口url
            type: 'post' //默认post
        }
    });
 
    //创建一个编辑器  id=LAY_demo_editor
    var editIndex = layedit.build('content');
    form.verify({
        content: function(value) { 
             return layedit.sync(editIndex);
            }
    });
    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/helpMgr/add", function (data) {
            Feng.success("添加成功！");

            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);
            
            //关掉对话框
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("添加失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
    
});