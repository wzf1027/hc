layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformWithDrawStatistics = {
        tableId: "platformWithDrawStatisticsTable",    //表格id
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
    platformWithDrawStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'datatime', sort: true, title: '日期',minWidth: 150},
            {field: 'totalSellerNumber', sort: true, title: '会员总提现'},
            {field: 'totalMemrchantNumber', sort: true, title: '商户总提现'},
            {field: 'totalAgentNumber', sort: true, title: '代理商总提现'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformWithDrawStatistics.tableId,
        url: Feng.ctxPath + '/platformWithdrawStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformWithDrawStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformWithDrawStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformWithDrawStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformWithDrawStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformWithDrawStatistics.search = function () {
        var queryData = {};
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(platformWithDrawStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformWithDrawStatistics.search();
    });
    
    
});
