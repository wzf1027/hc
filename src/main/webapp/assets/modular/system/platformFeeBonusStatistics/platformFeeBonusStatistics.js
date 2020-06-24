layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformFeeBonusStatistics = {
        tableId: "platformFeeBonusStatisticsTable",    //表格id
        condition: {
            timeLimit: ""
        }
    };


    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true
    });
    
    /**
     * 初始化表格的列
     */
    platformFeeBonusStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'datatime', sort: true, title: '日期',minWidth: 150},
            {field: 'totalNumber', sort: true, title: '平台接单总流水',minWidth: 160},
            {field: 'totalSellerBonusNumber', sort: true, title: '会员接单总挖矿',minWidth: 160},
            {field: 'totalMerchantFeeRatio', sort: true, title: '商户总费率(商户总手续费)',minWidth: 160},
            {field: 'totalAgentBounsNumber', sort: true, title: '代理商总挖矿',minWidth: 160},
            {field: 'totalTeamBonusNumber', sort: true, title: '团队推荐总挖矿',minWidth: 160},
            {field: 'totalSupReturnNumber', sort: true, title: '推荐总挖矿',minWidth: 200},
            {field: 'totalAccepterBonusNumber', sort: true, title: '承兑商总挖矿(包含承兑推荐承兑返利及承兑购买返利)',minWidth: 380},
            {field: 'totalOtcFeePriceSeller', sort: true, title: '会员出售总手续费',minWidth: 200},
            {field: 'totalOtcFeePriceAgent', sort: true, title: '代理手动出售总手续费',minWidth: 200},
            {field: 'totalOtcFeePriceMerchant', sort: true, title: '商户手动出售总手续费',minWidth: 200},
            {field: 'totalSellerExchangeFee', sort: true, title: '会员兑换总手续费',minWidth: 200},
            {field: 'totalAgentExchangeFee', sort: true, title: '代理兑换总手续费',minWidth: 200},
            {field: 'totalMerchantExchangeFee', sort: true, title: '商户兑换总手续费',minWidth: 200},
            {field: 'totalSellerWithdrawFee', sort: true, title: '会员提币总手续费',minWidth: 200},
            {field: 'totalAgentWithdrawFee', sort: true, title: '代理提币总手续费',minWidth: 200},
            {field: 'totalMerchantWithdrawFee', sort: true, title: '商户提币总手续费',minWidth: 200},
            {field: 'totalProfit', sort: true, title: '平台总盈利',minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformFeeBonusStatistics.tableId,
        url: Feng.ctxPath + '/platformFeeBonusStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformFeeBonusStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformFeeBonusStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformFeeBonusStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformFeeBonusStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformFeeBonusStatistics.search = function () {
        var queryData = {};
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(platformFeeBonusStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformFeeBonusStatistics.search();
    });
    
    
});
