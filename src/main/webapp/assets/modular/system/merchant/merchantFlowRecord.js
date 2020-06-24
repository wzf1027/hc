layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
	 var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var flowRecord = {
        tableId: "flowRecordTable",    //表格id
        condition: {
            name: "",
            deptId: "",
            timeLimit: ""
        }
    };

    /**
     * 初始化表格的列
     */
    flowRecord.initColumn = function () {
        return [[
            {field: 'source', sort: true, title: '来源'},
            {field: 'price', sort: true, title: '数量'},
            {field: 'createTime', sort: true, title: '创建时间'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + flowRecord.tableId,
        url: Feng.ctxPath + '/merchantFlowRecordMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: flowRecord.initColumn()
    });

    flowRecord.openExchange= function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:['500px','400px'],
            title: '一键兑换',
            content: Feng.ctxPath + '/mgr/exchange',
            end: function () {
                admin.getTempData('formOk');
                window.location.reload();
            }
        });
    };
    
    // 添加按钮点击事件
    $('#btnExchange').click(function () {
    	flowRecord.openExchange();
    });

});
