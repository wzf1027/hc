layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformMerchantStatistics = {
        tableId: "platformMerchantStatisticsTable",    //表格id
        condition: {
            timeLimit: ""
        }
    };


    /**
     * 初始化表格的列
     */
    platformMerchantStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'account', sort: true,title: '商户账号ID',minWidth: 200},
            {field: 'orderTotalNumber', sort: true, title: '交易订单总数',minWidth: 130},
            {field: 'successTotalNumber', sort: true, title: '成功订单总数',minWidth: 130},
            {field: 'totalCannelNumber', sort: true, title: '取消订单总数',minWidth: 130},
            {field: 'totalPrice', sort: true, title: '交易总金额',minWidth: 130},
            {field: 'successTotalPrice', sort: true, title: '实付金额(成功的订单)',minWidth: 180},
            {field: 'totalFeePrice', sort: true, title: '接单交易手续费总额',minWidth: 180},
            {field: 'rechagerNumber', sort: true, title: '总入金HC',minWidth: 150},
            {field: 'availableBalance', sort: true, title: 'HC可用余额',minWidth: 150},
            {field: 'frozenBalance', sort: true, title: '结算进行中HC',minWidth: 180},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 300, fixed: 'right'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformMerchantStatistics.tableId,
        url: Feng.ctxPath + '/platformMerchantStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformMerchantStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformMerchantStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformMerchantStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformMerchantStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformMerchantStatistics.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        table.reload(platformMerchantStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformMerchantStatistics.search();
    });
    
    platformMerchantStatistics.onMerchantStatistisc = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['1020px', '800px'],
            title: '统计',
            content: Feng.ctxPath + '/merchantBillMgr/' + data,
            end: function () {
                admin.getTempData('formOk') && table.reload(platformMerchantStatistics.tableId);
            }
        });
    };
    
    platformMerchantStatistics.onMerchantCannelStatistisc = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['600px', '600px'],
            title: '通道分析',
            content: Feng.ctxPath + '/merchantChannelMgr/' + data,
            end: function () {
                admin.getTempData('formOk') && table.reload(platformMerchantStatistics.tableId);
            }
        });
    };
    
 // 工具条点击事件
    table.on('tool(' + platformMerchantStatistics.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'merchantStatistisc') {
        	platformMerchantStatistics.onMerchantStatistisc(data.userId);
        }else if(layEvent === 'merchantCannelStatistisc'){
        	platformMerchantStatistics.onMerchantCannelStatistisc(data.userId);
        }
    });
    
});
