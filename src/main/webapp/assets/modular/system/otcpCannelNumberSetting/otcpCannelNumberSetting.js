layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var otcpCannelNumberSetting = {
        tableId: "otcpCannelNumberSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    otcpCannelNumberSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'number', sort: true, title: '取消次数'},
            {field: 'time', sort: true, title: '可购买时间（小时）'},
            {field: 'minTime', sort: true, title: '确认收款时间（分）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + otcpCannelNumberSetting.tableId,
        url: Feng.ctxPath + '/otcpCannelNumberSettingMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: otcpCannelNumberSetting.initColumn()
    });


    otcpCannelNumberSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/otcpCannelNumberSettingMgr/otcpCannelNumberSetting_update/' + data.settingId,
            end: function () {
                admin.getTempData('formOk') && table.reload(otcpCannelNumberSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + otcpCannelNumberSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            otcpCannelNumberSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
