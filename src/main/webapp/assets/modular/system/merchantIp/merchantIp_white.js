layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    
    var merchantIp = {
        tableId: "merchantIpTable"    //表格id
    };

    /**
     * 点击查询按钮
     */
    merchantIp.search = function () {
        var queryData = {};
        queryData['account'] = $("#account").val();
        queryData['ipAddress'] = $("#ipAddress").val();
        table.reload(merchantIp.tableId, {where: queryData});
    };

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        merchantIp.search();
    });

    merchantIp.openAdd = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '添加',
            content: Feng.ctxPath + '/merchantIpMgr/merchantIp_add/1',
            end: function () {
                admin.getTempData('formOk') && table.reload(merchantIp.tableId);
            }
        });
    };

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        merchantIp.openAdd();
    });
    /**
     * 初始化表格的列
     */
        merchantIp.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'account', sort: true, title: '商户ID'},
            {field: 'ipAddress', sort: true, title: 'ip地址'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + merchantIp.tableId,
        url: Feng.ctxPath + '/merchantIpMgr/list/1',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: merchantIp.initColumn()
    });




    merchantIp.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/merchantIpMgr/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(merchantIp.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id", data.id);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    };
    
    // 工具条点击事件
    table.on('tool(' + merchantIp.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'delete') {
            merchantIp.onDelete(data);
        }
    });

   
});
