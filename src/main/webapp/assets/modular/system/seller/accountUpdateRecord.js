layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var accountUpdateRecord = {
        tableId: "accountUpdateRecordTable"   //表格id
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
    accountUpdateRecord.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'serialno', sort: true,title: '会员流水订单号', minWidth: 250},
            {field: 'phone', sort: true,title: '会员账号', minWidth: 250},
            {field: 'source', sort: true,title: '资产'},
            {field: 'CODE', sort: true,title: '币种'},
            {field: 'type', sort: true,title: '类型'},
            {field: 'payMethodType', sort: true,title: '通道'},
            {field: 'beforPrice', sort: true,title: '变动前金额'},
            {field: 'afterPrice', sort: true,title: '变动后金额'},
            {field: 'price', sort: true,title: '变动金额'},
            {field: 'remark', sort: true,title: '备注'},
            {field: 'createTime', sort: true, title: '变动时间', minWidth: 160}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + accountUpdateRecord.tableId,
        url: Feng.ctxPath + '/sellerAccountUpdateMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: accountUpdateRecord.initColumn()
    });
    
    /**
     * 点击查询按钮
     */
    accountUpdateRecord.search = function () {
        var queryData = {};
        queryData['serialno'] = $("#serialno").val();
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['payMethodType'] = $('#payMethodType option:selected').val();
        queryData['source'] = $('#source option:selected').val();
        queryData['code'] = $('#code option:selected').val();
        queryData['type'] = $('#type option:selected').val();
        table.reload(accountUpdateRecord.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	accountUpdateRecord.search();
    });
    
    
    /**
     * 导出excel按钮
     */
    accountUpdateRecord.exportExcel = function () {
        var checkRows = table.checkStatus(accountUpdateRecord.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	accountUpdateRecord.exportExcel();
    });
   
    
});
