layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var sellerOrderTimeSetting = {
        tableId: "sellerOrderTimeSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    sellerOrderTimeSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'starTime', sort: true, title: '待解冻时间（分钟）'},
            {field: 'endTime', sort: true, title: '确认收款失效时间（分钟）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerOrderTimeSetting.tableId,
        url: Feng.ctxPath + '/sellerOrderTimeMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerOrderTimeSetting.initColumn()
    });


    sellerOrderTimeSetting.onEditAccepterRateSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/sellerOrderTimeMgr/sellerOrderTimeSetting_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(sellerOrderTimeSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + sellerOrderTimeSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            sellerOrderTimeSetting.onEditAccepterRateSetting(data);
        }
    });
   
});
