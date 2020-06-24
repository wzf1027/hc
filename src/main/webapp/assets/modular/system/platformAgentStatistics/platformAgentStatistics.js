layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformAgentStatistics = {
        tableId: "platformAgentStatisticsTable",    //表格id
        condition: {
            timeLimit: ""
        }
    };


    /**
     * 初始化表格的列
     */
    platformAgentStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'account', sort: true,title: '代理商账号ID',minWidth: 160},
            {field: 'totalOtcpAvailaBleBalance', sort: true, title: 'HC余额',minWidth: 130},
            {field: 'totalOtcpFrozenBalance', sort: true, title: 'HC结算进行中',minWidth: 130},
            {field: 'totalUsdtAvailaBleBalance', sort: true, title: 'USDT余额',minWidth: 130},
            {field: 'totalUsdtFrozenBalance', sort: true, title: 'USDT冻结',minWidth: 130},
            {field: 'totalOtcProfitNumber', sort: true, title: 'HC总收益',minWidth: 180}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformAgentStatistics.tableId,
        url: Feng.ctxPath + '/platformAgentStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformAgentStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformAgentStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformAgentStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformAgentStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformAgentStatistics.search = function () {
        var queryData = {};
        queryData['account'] = $("#account").val();
        table.reload(platformAgentStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformAgentStatistics.search();
    });
    
});
