layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    var laydate = layui.laydate;

    var chargerCoinAppeal = {
        tableId: "userChargerCoinAppealTable"    //表格id
    };

  //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        type: 'datetime'
    });
    
    
    /**
     * 点击查询按钮
     */
    chargerCoinAppeal.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['serialno'] = $("#serialno").val();
        queryData['hashValue'] = $("#hashValue").val();
        queryData['status'] = $('#status option:selected').val();
        table.reload(chargerCoinAppeal.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	chargerCoinAppeal.search();
    });
    
    /**
     * 导出excel按钮
     */
    chargerCoinAppeal.exportExcel = function () {
        var checkRows = table.checkStatus(chargerCoinAppeal.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	chargerCoinAppeal.exportExcel();
    });

    
    /**
     * 初始化表格的列
     */
    chargerCoinAppeal.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'appealId', hide: true, sort: true, title: 'id'},
            {field: 'serialno', sort: true, title: '流水号', minWidth: 200},
            {field: 'accountCode', sort: true, title: '用户ID', minWidth: 160},
            {field: 'phone', sort: true, title: '手机号码', minWidth: 160},
            {field: 'number', sort: true, title: '充值数量'},
            {field: 'hashValue', sort: true, title: 'hashValue值', minWidth: 250},
            {field: 'statusName', sort: true,  title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 160},
            {field: 'userAccount', sort: true, title: '操作人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + chargerCoinAppeal.tableId,
        url: Feng.ctxPath + '/userChargerCoinAppealMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: chargerCoinAppeal.initColumn()
    });
    
    chargerCoinAppeal.openAddChagerCoinAppeal = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '充USDT币',
            content: Feng.ctxPath + '/userChargerCoinAppealMgr/chargerCoinAppeal_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(chargerCoinAppeal.tableId);
            }
        });
    };
    
    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	chargerCoinAppeal.openAddChagerCoinAppeal();
    });
    
    chargerCoinAppeal.yesStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/userChargerCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核通过!");
                table.reload(chargerCoinAppeal.tableId);
            }, function (data) {
                Feng.error("操作失败!" + data.responseJSON.message + "!");
            });
            var dataStr = {'appealId':data.appealId,"status":1}
            ajax.setData(dataStr);            
            ajax.start();
        };
        Feng.confirm("是否确认审核通过?", operation);
    };
    
    chargerCoinAppeal.noStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/userChargerCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核不通过!");
                table.reload(chargerCoinAppeal.tableId);
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
    table.on('tool(' + chargerCoinAppeal.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'yesStatus') {
        	chargerCoinAppeal.yesStatus(data);
        } 
        if (layEvent === 'noStatus') {
        	chargerCoinAppeal.noStatus(data);
        } 
    });
    
});
