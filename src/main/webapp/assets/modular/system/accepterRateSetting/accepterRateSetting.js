layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var AccepterRateSetting = {
        tableId: "accepterRateSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    AccepterRateSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'rebateId', hide: true, sort: true, title: 'rebateId'},
            {field: 'value', sort: true, title: '返利比例（%）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AccepterRateSetting.tableId,
        url: Feng.ctxPath + '/accepterRateSettingMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AccepterRateSetting.initColumn()
    });
    
    
    AccepterRateSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/accepterRateSettingMgr/accepterReteSetting_update/' + data.rebateId,
            end: function () {
                admin.getTempData('formOk') && table.reload(AccepterRateSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + AccepterRateSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	AccepterRateSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
