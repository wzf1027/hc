layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var uSDTOtcpExchange = {
        tableId: "usdtOtcpExchangeTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    uSDTOtcpExchange.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'exchangeId', hide: true, sort: true, title: 'exchangeId'},
            {field: 'value', sort: true, title: 'USDT/HC'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + uSDTOtcpExchange.tableId,
        url: Feng.ctxPath + '/uSDTOtcpExchangeMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: uSDTOtcpExchange.initColumn()
    });
    
    
    uSDTOtcpExchange.onEditUSDTOtcpExchange = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/uSDTOtcpExchangeMgr/usdtOtcpExchange_update/' + data.exchangeId,
            end: function () {
                admin.getTempData('formOk') && table.reload(uSDTOtcpExchange.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + uSDTOtcpExchange.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	uSDTOtcpExchange.onEditUSDTOtcpExchange(data);
        }
    });
   
});
