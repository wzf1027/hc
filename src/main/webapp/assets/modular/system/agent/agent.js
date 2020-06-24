layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var Agent = {
        tableId: "agentTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    Agent.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'accountCode', sort: true, title: '用户ID', minWidth: 160},
            {field: 'account', sort: true, title: '登录账号', minWidth: 160},
            {field: 'name', sort: true, title: '姓名', minWidth: 160},
            {field: 'phone', sort: true, title: '电话', minWidth: 160},
            {field: 'usdtBalance', sort: true, title: 'USDT可用余额'},
            {field: 'usdtFrozen', sort: true, title: 'USDT冻结余额'},
            {field: 'otcpBalance', sort: true, title: 'HC可用余额'},
            {field: 'otcpFrozen', sort: true, title: 'HC冻结余额'},
            {field: 'merchantNumber', sort: true, title: '商户数量'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 250}
            ]];
    };


    /**
     * 点击查询按钮
     */
    Agent.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(Agent.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Agent.tableId,
        url: Feng.ctxPath + '/agentMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Agent.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        type: 'datetime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	Agent.search();
    });

 // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	Agent.openAddUser();
    });

    /**
     * 弹出添加用户对话框
     */
    Agent.openAddUser = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:['auto','400px'],
            title: '添加用户',
            content: Feng.ctxPath + '/agentMgr/agent_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(Agent.tableId);
            }
        });
    };
    
});
