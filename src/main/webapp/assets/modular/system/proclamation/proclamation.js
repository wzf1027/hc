layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    
    var proclamation = {
        tableId: "proclamationTable"    //表格id
    };

    
    proclamation.openAddProclamation = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '添加',
            content: Feng.ctxPath + '/proclamationMgr/proclamation_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(proclamation.tableId);
            }
        });
    };

    
    /**
     * 初始化表格的列
     */
    proclamation.initColumn = function () {
        return [[
            {field: 'proclamationId', hide: true, sort: true, title: 'id'},
            {field: 'title', sort: true, title: '名称'},
            {field: 'isTop', sort: true, title: '是否首页显示',templet:function(d){
            	var value = d.isTop;
            	if(value ==1){
            		return '是';
            	}
            	return '否';
            }},
            {field: 'createTime', sort: true, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + proclamation.tableId,
        url: Feng.ctxPath + '/proclamationMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: proclamation.initColumn()
    });


    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	proclamation.openAddProclamation();
    });
    
    proclamation.onEditProclamation= function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            area:["800px","800px"],
            content: Feng.ctxPath + '/proclamationMgr/proclamation_update/' + data.proclamationId,
            end: function () {
                admin.getTempData('formOk') && table.reload(proclamation.tableId);
            }
        });
    };

    proclamation.onDeleteProclamation= function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/proclamationMgr/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(proclamation.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("proclamationId", data.proclamationId);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    };
    
    // 工具条点击事件
    table.on('tool(' + proclamation.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
        	proclamation.onEditProclamation(data);
        } else if (layEvent === 'delete') {
        	proclamation.onDeleteProclamation(data);
        }
    });

   
});
