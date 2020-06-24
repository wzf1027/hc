layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var wallterLog = {
        tableId: "wallterLogTable"    //表格id
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
    wallterLog.initColumn = function () {
        return [[
        	{type: 'checkbox'},
        	{field: 'account', sort: true, title: '会员账户ID',minWidth: 150},
            {field: 'logUser', sort: true, title: '操作人'},
            {field: 'content', sort: true, title: '操作内容'},
            {field: 'createTime', sort: true, title: '时间'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + wallterLog.tableId,
        url: Feng.ctxPath + '/wallterLogMgr/list',
        page: true,
        height: "full-158",
        limits:[10,20,40,90,1000,5000,10000,100000,1000000],
        cellMinWidth: 100,
        cols: wallterLog.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    wallterLog.exportExcel = function () {
        var checkRows = table.checkStatus(wallterLog.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	wallterLog.exportExcel();
    });
    
    /**
     * 点击查询按钮
     */
    wallterLog.search = function () {
        var queryData = {};
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['phone'] = $("#phone").val();
        table.reload(wallterLog.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	wallterLog.search();
    });
    
    
});
