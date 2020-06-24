layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var platformCoinAddress = {
        tableId: "platformCoinAddressTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    platformCoinAddress.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'coinAddressId', hide: true, sort: true, title: 'coinAddressId'},
            {field: 'address', sort: true, title: 'USDT地址'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformCoinAddress.tableId,
        url: Feng.ctxPath + '/platformCoinAddressMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: platformCoinAddress.initColumn()
    });
    
    
    platformCoinAddress.onEditPlatformCoinAddress = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/platformCoinAddressMgr/platformCoinAddress_update/' + data.coinAddressId,
            end: function () {
                admin.getTempData('formOk') && table.reload(platformCoinAddress.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + platformCoinAddress.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	platformCoinAddress.onEditPlatformCoinAddress(data);
        }
    });
   
});
