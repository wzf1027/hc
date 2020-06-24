layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformRechargeStatistics = {
        tableId: "platformRechargeStatisticsTable",    //表格id
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
    platformRechargeStatistics.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'datatime', sort: true, title: '日期',minWidth: 150},
            {field: 'totalSellerNumber', sort: true, title: '会员总入金'},
            {field: 'totalUserNumber', sort: true, title: '商户总入金'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformRechargeStatistics.tableId,
        url: Feng.ctxPath + '/platformRechargeStatisticsMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: platformRechargeStatistics.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    platformRechargeStatistics.exportExcel = function () {
        var checkRows = table.checkStatus(platformRechargeStatistics.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	platformRechargeStatistics.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    platformRechargeStatistics.search = function () {
        var queryData = {};
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(platformRechargeStatistics.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	platformRechargeStatistics.search();
    });
    
    
});
