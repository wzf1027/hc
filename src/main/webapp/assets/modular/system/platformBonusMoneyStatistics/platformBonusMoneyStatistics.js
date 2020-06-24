layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformBonusMoneyStatistics = {
        tableId: "platformBonusMoneyStatisticsTable",    //表格id
        condition: {
            timeLimit: ""
        }
    };


    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        type: 'datetime'
    });
    
    /**
     * 初始化表格的列
     */
    platformBonusMoneyStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'datatime', sort: true, title: '日期',minWidth: 160},
            {field: 'totalSellerNumber', sort: true, title: '会员总挖矿'},
            {field: 'totalAgentBonusNumber', sort: true, title: '代理总挖矿'},
            {field: 'totalRecommendNumber', sort: true, title: '推荐总挖矿'},
            {field: 'totalAccepterNumber', sort: true, title: '承兑商总挖矿'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformBonusMoneyStatistics.tableId,
        url: Feng.ctxPath + '/platformBonusMoneyStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformBonusMoneyStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformBonusMoneyStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformBonusMoneyStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformBonusMoneyStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformBonusMoneyStatistics.search = function () {
        var queryData = {};
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(platformBonusMoneyStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformBonusMoneyStatistics.search();
    });
    
    
});
