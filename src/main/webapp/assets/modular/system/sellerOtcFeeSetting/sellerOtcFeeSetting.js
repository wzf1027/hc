layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var sellerOtcFeeSetting = {
        tableId: "sellerOtcFeeSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    sellerOtcFeeSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'settingId', hide: true, sort: true, title: 'settingId'},
            {field: 'ratio', sort: true, title: '手续费比例（%）'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerOtcFeeSetting.tableId,
        url: Feng.ctxPath + '/sellerOtcFeeSettingMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerOtcFeeSetting.initColumn()
    });
    
    
    sellerOtcFeeSetting.onEdit = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            content: Feng.ctxPath + '/sellerOtcFeeSettingMgr/sellerOtcFeeSetting_update/' + data.settingId,
            end: function () {
                admin.getTempData('formOk') && table.reload(sellerOtcFeeSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + sellerOtcFeeSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	sellerOtcFeeSetting.onEdit(data);
        }
    });
   
});
