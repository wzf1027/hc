layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var merchantPay = {
        tableId: "merchantPayTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    merchantPay.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'value', sort: true, title: '商户账号'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + merchantPay.tableId,
        url: Feng.ctxPath + '/merchantPayMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: merchantPay.initColumn()
    });
    
    
    merchantPay.onEditMerchantPay = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/merchantPayMgr/merchantPay_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(merchantPay.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + merchantPay.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	merchantPay.onEditMerchantPay(data);
        }
    });
   
});
