layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var sellOtcpOrder = {
        tableId: "sellOtcpOrderTable",    //表格id
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
            {field: 'phone', sort: true, title: '卖家账号',minWidth: 160},
            {field: 'serialno', sort: true, title: '订单流水号', minWidth: 200},
            {field: 'price', sort: true, title: '出售单价'},
            {field: 'number',hide: true, sort: true, title: '出售总数量'},
            {field: 'supNumber', title: '剩余数量'},
            {field: 'minNumber', sort: true, title: '最小限额'},
            {field: 'maxNumber', sort: true, title: '最大限额'},
            {field: 'feePrice', sort: true,title: '手续费'},
            {field: 'statusName', sort: true,title: '状态'},
            {field: 'typeName', sort: true,title: '出售角色'},
            {field: 'createTime', sort: true, title: '出售时间', minWidth: 160}
            ,{align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellOtcpOrder.tableId,
        url: Feng.ctxPath + '/sellOtcpOrderMgr/list',
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
        queryData['roleType'] = $('#roleType option:selected') .val();;
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(sellOtcpOrder.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellOtcpOrder.search();
    });
    
    /**
     * 弹出添加通知
     */
    sellOtcpOrder.openAddSell = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '自动出售',
            content: Feng.ctxPath + '/sellOtcpOrderMgr/auto_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(sellOtcpOrder.tableId);
            }
        });
    };
    
    sellOtcpOrder.openAddnoAutoSell = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '手动出售',
            content: Feng.ctxPath + '/sellOtcpOrderMgr/no_auto_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(sellOtcpOrder.tableId);
            }
        });
    };
    
    // 添加按钮点击事件
    $('#btnAutoAdd').click(function () {
    	sellOtcpOrder.openAddSell();
    });
    
 // 添加按钮点击事件
    $('#btnNoAutoAdd').click(function () {
    	sellOtcpOrder.openAddnoAutoSell();
    });
    
    var isFirst = true;
    sellOtcpOrder.onCannelTrade = function (data) {
        var operation = function () {
        	if(isFirst){
          	  	isFirst = false;
        		 var ajax = new $ax(Feng.ctxPath + "/sellOtcpOrderMgr/revocation", function (data) {
                 	table.reload(sellOtcpOrder.tableId);
                 	if(data.success){
                 	   Feng.success("取消成功!");    
                    }else{
                 	   Feng.error("取消失败!" + data.message + "!");
                    }   
                 	isFirst =true
                 }, function (data) {
                     Feng.error("取消失败!" + data.responseJSON.message + "!");
                     isFirst =true
                 });
                 ajax.set("orderId", data.orderId);
                 ajax.start();
        	}
        };
        Feng.confirm("是否撤销订单?", operation);
    };
    
    
 // 工具条点击事件
    table.on('tool(' + sellOtcpOrder.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent === 'cannelTrade'){
        	sellOtcpOrder.onCannelTrade(data);
        }
    });
    
});
