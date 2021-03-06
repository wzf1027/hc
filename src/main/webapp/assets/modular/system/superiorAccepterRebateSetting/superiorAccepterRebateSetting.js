layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var SuperiorAccepterRebateSetting = {
        tableId: "superiorAccepterRebateSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    SuperiorAccepterRebateSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'settingId', hide: true, sort: true, title: 'settingId'},
            {field: 'value', sort: true, title: '返利比例（%）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + SuperiorAccepterRebateSetting.tableId,
        url: Feng.ctxPath + '/superiorAccepterRebateSettingMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: SuperiorAccepterRebateSetting.initColumn()
    });
    
    
    SuperiorAccepterRebateSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/superiorAccepterRebateSettingMgr/superiorAccepterRebateSetting_update/' + data.settingId,
            end: function () {
                admin.getTempData('formOk') && table.reload(SuperiorAccepterRebateSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + SuperiorAccepterRebateSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	SuperiorAccepterRebateSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
