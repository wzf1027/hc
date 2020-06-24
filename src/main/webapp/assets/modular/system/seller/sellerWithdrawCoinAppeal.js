layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    var laydate = layui.laydate;
    
  //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        type: 'datetime'
    });

    var sellerWithdrawCoinAppeal = {
        tableId: "sellerWithdrawCoinAppealTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    sellerWithdrawCoinAppeal.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'appealId', hide: true, sort: true, title: 'id'},
            {field: 'phone', sort: true, title: '账号', minWidth: 160},
            {field: 'number', sort: true, title: '提现数量'},
            {field: 'address', sort: true, title: '提现地址', minWidth: 200},
            {field: 'feePrice', sort: true, title: '提现手续费'},
            {field: 'statusName', sort: true,  title: '状态'},
            {field: 'totalNumber', sort: true,  title: '实际转账数量'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 160},
            {field: 'userAccount', sort: true, title: '操作人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerWithdrawCoinAppeal.tableId,
        url: Feng.ctxPath + '/sellerWithdrawCoinAppealMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerWithdrawCoinAppeal.initColumn()
    });
    /**
     * 点击查询按钮
     */
    sellerWithdrawCoinAppeal.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['address'] = $("#address").val();
        queryData['status'] = $('#status option:selected').val();
        table.reload(sellerWithdrawCoinAppeal.tableId, {where: queryData});
    };
    
    
    // 搜索按钮点击事件
       $('#btnSearch').click(function () {
    	   sellerWithdrawCoinAppeal.search();
       });
       
       /**
        * 导出excel按钮
        */
       sellerWithdrawCoinAppeal.exportExcel = function () {
           var checkRows = table.checkStatus(sellerWithdrawCoinAppeal.tableId);
           if (checkRows.data.length === 0) {
               Feng.error("请选择要导出的数据");
           } else {
               table.exportFile(tableResult.config.id, checkRows.data, 'xls');
           }
       };
       
    // 导出excel
       $('#btnExp').click(function () {
    	   sellerWithdrawCoinAppeal.exportExcel();
       });

    
    sellerWithdrawCoinAppeal.yesStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerWithdrawCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核通过!");
                table.reload(sellerWithdrawCoinAppeal.tableId);
            }, function (data) {
                Feng.error("操作失败!" + data.responseJSON.message + "!");
            });
            var dataStr = {'appealId':data.appealId,"status":1}
            ajax.setData(dataStr);            
            ajax.start();
        };
        Feng.confirm("是否确认审核通过?", operation);
    };
    
    sellerWithdrawCoinAppeal.noStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerWithdrawCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核不通过!");
                table.reload(sellerWithdrawCoinAppeal.tableId);
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
    table.on('tool(' + sellerWithdrawCoinAppeal.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'yesStatus') {
        	sellerWithdrawCoinAppeal.yesStatus(data);
        } 
        if (layEvent === 'noStatus') {
        	sellerWithdrawCoinAppeal.noStatus(data);
        } 
    });
    
});
