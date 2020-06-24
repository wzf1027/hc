layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var sellOtcpOrder = {
        tableId: "buyCoinOrderTable",    //表格id
        condition: {
            phone: "",
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
    sellOtcpOrder.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'orderId', hide: true, sort: true, title: 'orderId'},
            {field: 'serialno', sort: true,title: '流水订单号', minWidth: 200},
            {field: 'buyerAccount', sort: true,title: '商户账号', minWidth: 160},
            {field: 'number', sort: true,title: '交易金额'},
            {field: 'agentFeeRatio', sort: true,title: '手续费率'},
            {field: 'agentFee', sort: true,title: '返佣金额'},
            {field: 'typeName', sort: true,title: '支付通道'},
            {field: 'statusName', sort: true,title: '状态'},
            {field: 'closeTime', sort: true, title: '完成时间', minWidth: 160}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellOtcpOrder.tableId,
        url: Feng.ctxPath + '/merchantBuyCoinnMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: sellOtcpOrder.initColumn()
    });
    
    /**
     * 点击查询按钮
     */
    sellOtcpOrder.search = function () {
        var queryData = {};
        queryData['account'] = $("#account").val();
        queryData['serialno'] = $("#serialno").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['payMethodType'] = $('#payMethodType option:selected') .val();
        table.reload(sellOtcpOrder.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellOtcpOrder.search();
    });
    
    
    /**
     * 导出excel按钮
     */
    sellOtcpOrder.exportExcel = function () {
        var checkRows = table.checkStatus(sellOtcpOrder.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	sellOtcpOrder.exportExcel();
    });

    
});
