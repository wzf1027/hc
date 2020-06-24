layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var sellOtcpOrder = {
        tableId: "sellerOrderTable",    //表格id
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
            {field: 'orderId', hide: true, sort: true, title: 'orderId'},
            {field: 'account', sort: true, title: '会员账号',minWidth: 160},
            {field: 'phone', sort: true, title: '手机号码',minWidth: 160},
            {field: 'nickName', sort: true, title: '昵称'},
            {field: 'account', sort: true, title: '推荐码'},
            {field: 'number', sort: true, title: '出售价格'},
            {field: 'statusName', sort: true,title: '状态'},
            {field: 'createTime', sort: true, title: '出售时间', minWidth: 160}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellOtcpOrder.tableId,
        url: Feng.ctxPath + '/sellerOrderMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellOtcpOrder.initColumn()
    });
    
    /**
     * 点击查询按钮
     */
    sellOtcpOrder.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(sellOtcpOrder.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellOtcpOrder.search();
    });
    
    
});
