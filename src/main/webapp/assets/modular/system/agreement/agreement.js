layui.use(['form', 'upload', 'element', 'ax', 'laydate'], function () {
    var $ = layui.jquery;
    var form = layui.form;
    var upload = layui.upload;
    var element = layui.element;
    var $ax = layui.ax;
    var laydate = layui.laydate;

    
    var ajax = new $ax(Feng.ctxPath + "/agreementMgr/getAgreementDetail");
    var result = ajax.start();

    //用这个方法必须用在class有layui-form的元素上
    form.val('agreementForm', result.data);
    

    //表单提交事件
    form.on('submit(agreementSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/agreementMgr/saveOrUpdate", function (data) {
            Feng.success("更新保存成功!");
        }, function (data) {
            Feng.error("更新保存失败!" + data.responseJSON.message + "!");
        });
        ajax.set(data.field);
        ajax.start();
    });
});