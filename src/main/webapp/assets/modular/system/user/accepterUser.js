layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var accepterUser = {
        tableId: "accepterUserTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    accepterUser.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'userId', hide: true, sort: true, title: '用户id'},
            {field: 'account', sort: true, title: '账号', minWidth: 160},
            {field: 'name', sort: true, title: '姓名', minWidth: 160},
            {field: 'phone', sort: true, title: '手机号码', minWidth: 160},
            {field: 'status', sort: true, title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + accepterUser.tableId,
        url: Feng.ctxPath + '/accepterUserMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: accepterUser.initColumn()
    });
    
});
