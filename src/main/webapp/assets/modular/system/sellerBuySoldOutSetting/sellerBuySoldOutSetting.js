layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var sellerBuySoldOutSetting = {
        tableId: "sellerBuySoldOutSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    sellerBuySoldOutSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'number', sort: true, title: '失败次数'},
            {field: 'time', sort: true, title: '上架时间（小时）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerBuySoldOutSetting.tableId,
        url: Feng.ctxPath + '/sellerBuySoldOutMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerBuySoldOutSetting.initColumn()
    });


    sellerBuySoldOutSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/sellerBuySoldOutMgr/sellerBuySoldOutSetting_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(sellerBuySoldOutSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + sellerBuySoldOutSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            sellerBuySoldOutSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
