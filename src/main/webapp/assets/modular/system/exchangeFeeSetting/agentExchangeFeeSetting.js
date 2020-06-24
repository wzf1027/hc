layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var agentExchangeFeeSetting = {
        tableId: "agentExchangeFeeSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    agentExchangeFeeSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'exchangeValue', sort: true, title: '手续费比例（%）'},
            {field: 'type', sort: true, title: '兑换方向'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + agentExchangeFeeSetting.tableId,
        url: Feng.ctxPath + '/exchangeFeeSettingMgr/list/4',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: agentExchangeFeeSetting.initColumn()
    });
    
    
    agentExchangeFeeSetting.onEdit = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/exchangeFeeSettingMgr/exchangeFeeSetting_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(agentExchangeFeeSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + agentExchangeFeeSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	agentExchangeFeeSetting.onEdit(data);
        }
    });
   
});
