layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    var userId =$("#userId").val();
    var merchantBill = {
        tableId: "merchantBillTable",    //表格id
        condition: {
            phone: "",
            timeLimit: ""
        }
    };


    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit'
    });
    
    /**
     * 初始化表格的列
     */
    merchantBill.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'createTime', sort: true, title: '日期',minWidth: 150},
            {field: 'orderTotalNumber', sort: true, title: '订单总数'},
            {field: 'successTotalNumber', sort: true, title: '成功订单数'},
            {field: 'noPayOrderNumber', sort: true, title: '未支付订单数'},
            {field: 'totalCannelNumber', sort: true,title: '取消订单数(包含申诉中订单数)',minWidth: 250},
            {field: 'successTotalPrice', sort: true,title: '成功订单总额'},
            {field: 'totalPrice', sort: true, title: '订单总额'},
            {field: 'totalFeePrice', sort: true, title: '手续费'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + merchantBill.tableId,
        url: Feng.ctxPath + '/merchantBillMgr/list/'+userId,
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: merchantBill.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    merchantBill.exportExcel = function () {
        var checkRows = table.checkStatus(merchantBill.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	merchantBill.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    merchantBill.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['userId'] = $("#userId").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(merchantBill.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	merchantBill.search();
    });
    
    
});
