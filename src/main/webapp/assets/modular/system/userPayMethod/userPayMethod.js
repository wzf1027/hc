layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;


    var userPayMethod = {
        tableId: "userPayMethodTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    userPayMethod.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'payMethodId', hide: true, sort: true, title: 'payMethodId'},
            {field: 'account', sort: true, title: '银行卡号'},
            {field: 'cardBank', sort: true, title: '开户行'},
            {field: 'cardBankName', sort: true, title: '银行名称'},
            {field: 'cardName', sort: true, title: '真实姓名'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    /**
     * 点击查询按钮
     */
    userPayMethod.search = function () {
        var queryData = {};
        queryData['condition'] = $("#condition").val();
        table.reload(userPayMethod.tableId, {where: queryData});
    };

    /**
     * 弹出添加
     */
    userPayMethod.openAddUserPayMethod = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加',
            content: Feng.ctxPath + '/userPayMethodMgr/userPayMethod_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(userPayMethod.tableId);
            }
        });
    };


    userPayMethod.onEditUserPayMethod = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '详情',
            content: Feng.ctxPath + '/userPayMethodMgr/userPayMethod_update/' + data.payMethodId,
            end: function () {
                admin.getTempData('formOk') && table.reload(userPayMethod.tableId);
            }
        });
    };

    /**
     * 点击删除通知
     *
     * @param data 点击按钮时候的行数据
     */
    userPayMethod.onDeleteUserPayMethod = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/userPayMethodMgr/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(userPayMethod.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("payMethodId", data.payMethodId);
            ajax.start();
        };
        Feng.confirm("是否删除 ?", operation);
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + userPayMethod.tableId,
        url: Feng.ctxPath + '/userPayMethodMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: userPayMethod.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	userPayMethod.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	userPayMethod.openAddUserPayMethod();
    });

    // 工具条点击事件
    table.on('tool(' + userPayMethod.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
        	userPayMethod.onEditUserPayMethod(data);
        } else if (layEvent === 'delete') {
        	userPayMethod.onDeleteUserPayMethod(data);
        }
    });
});
