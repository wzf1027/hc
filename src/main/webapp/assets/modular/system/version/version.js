layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    

    var Version = {
        tableId: "versionTable"    //表格id
    };


    
    /**
     * 初始化表格的列
     */
    Version.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'versionId', hide: true, sort: true, title: 'id'},
            {field: 'version', sort: true, title: '版本号'},
            {field: 'address', sort: true, title: '地址'},
            {field: 'type', sort: true, title: '更新端口'},
            {field: 'content', sort: true, title: '更新内容'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Version.tableId,
        url: Feng.ctxPath + '/versionMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Version.initColumn()
    });
    
    Version.onEditVersion = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['600px', '600px'],
            title: '编辑',
            content: Feng.ctxPath + '/versionMgr/version_update/' + data.versionId,
            end: function () {
                admin.getTempData('formOk') && table.reload(Version.tableId);
            }
        });
    };
    // 工具条点击事件
    table.on('tool(' + Version.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
        	Version.onEditVersion(data);
        }
    });
});
