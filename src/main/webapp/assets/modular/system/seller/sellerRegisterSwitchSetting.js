layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var sellerRegisterSwitch = {
        tableId: "sellerRegisterSwitchTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    sellerRegisterSwitch.initColumn = function () {
        return [[
            {field: 'switchSettingId', hide: true, sort: false, title: 'switchSettingId'},
            {field: 'isSwitch', sort: false, templet: '#statusTpl', title: '开关状态'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerRegisterSwitch.tableId,
        url: Feng.ctxPath + '/sellerRegisterSwitchSettingMgr/list',
        page: false,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerRegisterSwitch.initColumn()
    });
    
    sellerRegisterSwitch.changeStatus = function (switchSettingId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/sellerRegisterSwitchSettingMgr/freeze", function (data) {
                Feng.success("开启成功!");
            }, function (data) {
                Feng.error("开启失败!");
                table.reload(sellerRegisterSwitch.tableId);
            });
            ajax.set("switchSettingId", switchSettingId);
            ajax.start();
        } else {
            var ajax = new $ax(Feng.ctxPath + "/sellerRegisterSwitchSettingMgr/freeze", function (data) {
                Feng.success("关闭成功!");
            }, function (data) {
                Feng.error("关闭失败!" + data.responseJSON.message + "!");
                table.reload(sellerRegisterSwitch.tableId);
            });
            ajax.set("switchSettingId", switchSettingId);
            ajax.start();
        }
    };
    
    form.on('switch(status)', function (obj) {
        var switchSettingId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        sellerRegisterSwitch.changeStatus(switchSettingId, checked);
    });

});
