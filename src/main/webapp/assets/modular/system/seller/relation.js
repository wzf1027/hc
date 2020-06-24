layui.use(['layer', 'form', 'ztree', 'laydate', 'admin', 'ax', 'table', 'treetable'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    var table = layui.table;
    var treetable = layui.treetable;

    /**
     * 列表管理初始化
     */
    var Proxy = {
        tableId: "proxyTable",	//表格id
        condition: {
            proxyId: ""
        }
    };

    /**
     * 初始化表格的列
     */
    Proxy.initColumn = function () {
        return [[
            {type: 'numbers'},
            {field: 'id', hide: true, sort: true, title: 'id'},
            {field: 'account', sort: true, title: '会员账号ID'},
            {field: 'createTime', sort: true, title: '注册时间'}
        ]];
    };


    /**
     * 点击查询按钮
     */
    Proxy.search = function () {
        var queryData = {};
        queryData['condition'] = $("#condition").val();
        Proxy.initTable(Proxy.tableId, queryData);
    };



    /**
     * 导出excel按钮
     */
    Proxy.exportExcel = function () {
        var checkRows = table.checkStatus(Proxy.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };


    /**
     * 初始化表格
     */
    Proxy.initTable = function (tableId, data) {
        return treetable.render({
            elem: '#' + tableId,
            url: Feng.ctxPath + '/sellerMgr/listRelationTree',
            where: data,
            page: false,
            height: "full-158",
            cellMinWidth: 100,
            cols: Proxy.initColumn(),
            treeColIndex: 2,
            treeSpid: "0",
            treeIdName: 'id',
            treePidName: 'pId',
            treeDefaultClose: false,
            treeLinkage: true
        });
    };

    // 渲染表格
    var tableResult = Proxy.initTable(Proxy.tableId);

    $('#expandAll').click(function () {
        treetable.expandAll('#' + Proxy.tableId);
    });
    $('#foldAll').click(function () {
        treetable.foldAll('#' + Proxy.tableId);
    });



    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        Proxy.search();
    });


});
