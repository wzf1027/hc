layui.use(['form', 'upload', 'element', 'ax', 'laydate'], function () {
    var $ = layui.jquery;
    var form = layui.form;
    var upload = layui.upload;
    var element = layui.element;
    var $ax = layui.ax;
    var laydate = layui.laydate;


    //表单提交事件
    form.on('submit(bingGoogleSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/mgr/bingGoogle", function (data) {
            Feng.success("更新保存成功!");
            location.reload();
        }, function (data) {
            Feng.error("更新保存失败!" + data.responseJSON.message + "!");
        });
        ajax.set(data.field);
        ajax.start();
    });
});