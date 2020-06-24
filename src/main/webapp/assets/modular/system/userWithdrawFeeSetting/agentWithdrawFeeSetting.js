layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var userWithdrawFeeSetting = {
        tableId: "userWithdrawFeeSettingTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    userWithdrawFeeSetting.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'settingId', hide: true, sort: true, title: 'settingId'},
            {field: 'minNumber', sort: true, title: '最小提现额度'},
            {field: 'maxNumber', sort: true, title: '最大提现额度'},
            {field: 'minFeeNumber', sort: true, title: '最小提现手续费数量'},
            {field: 'startRatioNumber', sort: true, title: '超出该提现数量，提现手续比例算',minWidth: 280},
            {field: 'feeRatio', sort: true, title: '提现手续费比例'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + userWithdrawFeeSetting.tableId,
        url: Feng.ctxPath + '/userWithdrawFeeSettingMgr/list',
        page: true,
        where:{condition:'4'},
        height: "full-158",
        cellMinWidth: 100,
        cols: userWithdrawFeeSetting.initColumn()
    });
    
    
    userWithdrawFeeSetting.onEditSellerWithdrawFeeSetting = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            area:['700px','auto'],
            content: Feng.ctxPath + '/userWithdrawFeeSettingMgr/userWithdrawFeeSetting_update/' + data.settingId,
            end: function () {
                admin.getTempData('formOk') && table.reload(userWithdrawFeeSetting.tableId);
            }
        });
    };

    
    // 工具条点击事件
    table.on('tool(' + userWithdrawFeeSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'update') {
        	userWithdrawFeeSetting.onEditSellerWithdrawFeeSetting(data);
        }
    });
});
