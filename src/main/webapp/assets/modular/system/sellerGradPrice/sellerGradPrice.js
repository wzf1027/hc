layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var gradPriceSetting = {
        tableId: "gradPriceSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    gradPriceSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'cash', sort: true, title: '押金金额'},
            {field: 'priceRate', sort: true, title: '最大接单比例（%）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + gradPriceSetting.tableId,
        url: Feng.ctxPath + '/sellerGradPriceMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: gradPriceSetting.initColumn()
    });


    gradPriceSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/sellerGradPriceMgr/gradPriceSetting_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(gradPriceSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + gradPriceSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            gradPriceSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
