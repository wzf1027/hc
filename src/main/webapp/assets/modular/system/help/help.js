layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    
    var Help = {
        tableId: "helpTable"    //表格id
    };

    
    Help.openAddHelp = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '添加',
            content: Feng.ctxPath + '/helpMgr/help_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(Help.tableId);
            }
        });
    };

    
    /**
     * 初始化表格的列
     */
    Help.initColumn = function () {
        return [[
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'title', sort: true, title: '名称'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Help.tableId,
        url: Feng.ctxPath + '/helpMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Help.initColumn()
    });


    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	Help.openAddHelp();
    });
    
    Help.onEditHelp= function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            area:["800px","800px"],
            content: Feng.ctxPath + '/helpMgr/help_update/' + data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(Help.tableId);
            }
        });
    };

    Help.onDeleteHelp= function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/helpMgr/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(Help.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("helpId", data.id);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    };
    
    // 工具条点击事件
    table.on('tool(' + Help.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
        	Help.onEditHelp(data);
        } else if (layEvent === 'delete') {
        	Help.onDeleteHelp(data);
        }
    });

   
});
