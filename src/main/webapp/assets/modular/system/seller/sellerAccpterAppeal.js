layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    var laydate = layui.laydate;

    var sellerAccpterAppeal = {
        tableId: "sellerAccpterAppealTable"    //表格id
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
    sellerAccpterAppeal.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'appealId', hide: true, sort: true, title: 'id'},
            {field: 'account', sort: true, title: '会员账号', minWidth: 250},
            {field: 'name', sort: true, title: '真实姓名', minWidth: 250},
            {field: 'idCardNo', sort: true, title: '身份证号码', minWidth: 250},
            {field: 'phone', sort: true, title: '联系方式', minWidth: 250},
            {field: 'statusName', sort: true,  title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 250},
            {field: 'userAccount', sort: true, title: '操作人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerAccpterAppeal.tableId,
        url: Feng.ctxPath + '/sellerAccpterAppealMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerAccpterAppeal.initColumn()
    });
    
    
    /**
     * 点击查询按钮
     */
    sellerAccpterAppeal.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['idcardNo'] = $("#idcardNo").val();
        queryData['name'] = $("#name").val();
        queryData['status'] = $('#status option:selected').val();
        table.reload(sellerAccpterAppeal.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellerAccpterAppeal.search();
    });
    
    /**
     * 导出excel按钮
     */
    sellerAccpterAppeal.exportExcel = function () {
        var checkRows = table.checkStatus(sellerAccpterAppeal.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	sellerAccpterAppeal.exportExcel();
    });
    
    sellerAccpterAppeal.yesStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerAccpterAppealMgr/updateStatus", function (data) {
                Feng.success("审核通过!");
                table.reload(sellerAccpterAppeal.tableId);
            }, function (data) {
                Feng.error("操作失败!" + data.responseJSON.message + "!");
            });
            var dataStr = {'appealId':data.appealId,"status":1}
            ajax.setData(dataStr);            
            ajax.start();
        };
        Feng.confirm("是否确认审核通过?", operation);
    };
    
    sellerAccpterAppeal.noStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerAccpterAppealMgr/updateStatus", function (data) {
                Feng.success("审核不通过!");
                table.reload(sellerAccpterAppeal.tableId);
            }, function (data) {
                Feng.error("操作失败!" + data.responseJSON.message + "!");
            });
            var dataStr = {'appealId':data.appealId,"status":2}
            ajax.setData(dataStr);            
            ajax.start();
        };
        Feng.confirm("是否确认审核不通过?", operation);
    };

 // 工具条点击事件
    table.on('tool(' + sellerAccpterAppeal.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'yesStatus') {
        	sellerAccpterAppeal.yesStatus(data);
        } 
        if (layEvent === 'noStatus') {
        	sellerAccpterAppeal.noStatus(data);
        } 
    });
    
});
