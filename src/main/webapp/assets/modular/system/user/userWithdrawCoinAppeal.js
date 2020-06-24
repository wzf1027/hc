layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    var laydate = layui.laydate;

    var withdrawCoinAppeal = {
        tableId: "userWithdrawCoinAppealTable"    //表格id
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
    withdrawCoinAppeal.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'appealId', hide: true, sort: true, title: 'id'},
            {field: 'accountCode', sort: true, title: '用户ID', minWidth: 160},
            {field: 'phone', sort: true, title: '手机号码', minWidth: 160},
            {field: 'number', sort: true, title: '提现数量'},
            {field: 'address', sort: true, title: '提现地址', minWidth: 250},
            {field: 'feePrice', sort: true, title: '提现手续费'},
            {field: 'statusName', sort: true,  title: '状态'},
            {field: 'totalNumber', sort: true,  title: '实际打款数量'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 160},
            {field: 'userAccount', sort: true, title: '操作人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + withdrawCoinAppeal.tableId,
        url: Feng.ctxPath + '/userWithdrawCoinAppealMgr/list',
        where:{condition:'2'},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: withdrawCoinAppeal.initColumn()
    });
    
    /**
     * 点击查询按钮
     */
    withdrawCoinAppeal.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['address'] = $("#address").val();
        queryData['status'] = $('#status option:selected').val();
        table.reload(withdrawCoinAppeal.tableId, {where: queryData});
    };
    
    
    // 搜索按钮点击事件
       $('#btnSearch').click(function () {
    	   withdrawCoinAppeal.search();
       });
       
       /**
        * 导出excel按钮
        */
       withdrawCoinAppeal.exportExcel = function () {
           var checkRows = table.checkStatus(withdrawCoinAppeal.tableId);
           if (checkRows.data.length === 0) {
               Feng.error("请选择要导出的数据");
           } else {
               table.exportFile(tableResult.config.id, checkRows.data, 'xls');
           }
       };
       
    // 导出excel
       $('#btnExp').click(function () {
    	   withdrawCoinAppeal.exportExcel();
       });

    
    
    withdrawCoinAppeal.yesStatus = function (data) {
        layer.open({
            id:1,
            type: 1,
            title:'是否确认审核通过?',
            skin:'layui-layer-rim',
            area:['450px', 'auto'],
            closeBtn :1,//右上角的关闭按钮取消
            content: ' <div class="row" style="width: 420px;  margin-left:7px; margin-top:10px;">'
                +'<div class="col-sm-12">'
                +'<div class="input-group">'
                +'<span class="input-group-addon"> 二级密码:</span>'
                +'<input id="password" type="password" class="layui-input" placeholder="请输入密码">'
                +'</div>'
                +'</div>'
                +'</div>'
            ,
            btn:['确认'],
            btn1: function (index,layero) {
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/userWithdrawCoinAppealMgr/updateStatus", function (d) {
                    if(d.success){
                        Feng.success("审核通过!");
                    }else{
                        Feng.error("操作失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("操作失败!" + data.message + "!");
                });
                var dataStr = {'appealId':data.appealId,"status":1,"password":password};
                ajax.setData(dataStr);
                ajax.start();
                table.reload(withdrawCoinAppeal.tableId);
                layer.close(index);
            }
        });
        //
    	// var operation = function () {
        //     var ajax = new $ax(Feng.ctxPath + "/userWithdrawCoinAppealMgr/updateStatus", function (data) {
        //         Feng.success("审核通过!");
        //         table.reload(withdrawCoinAppeal.tableId);
        //     }, function (data) {
        //         Feng.error("操作失败!" + data.responseJSON.message + "!");
        //     });
        //     var dataStr = {'appealId':data.appealId,"status":1}
        //     ajax.setData(dataStr);
        //     ajax.start();
        // };
        // Feng.confirm("是否确认审核通过?", operation);
    };
    
    withdrawCoinAppeal.openAddWithdrawCoinAppeal = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:['500px','400px'],
            title: '提USDT币',
            content: Feng.ctxPath + '/userWithdrawCoinAppealMgr/withdrawCoinAppeal_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(withdrawCoinAppeal.tableId);
            }
        });
    };
    
    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	withdrawCoinAppeal.openAddWithdrawCoinAppeal();
    });
    
    withdrawCoinAppeal.noStatus = function (data) {
        layer.open({
            id:1,
            type: 1,
            title:'是否确认审核不通过?',
            skin:'layui-layer-rim',
            area:['450px', 'auto'],
            closeBtn :1,//右上角的关闭按钮取消
            content: ' <div class="row" style="width: 420px;  margin-left:7px; margin-top:10px;">'
                +'<div class="col-sm-12">'
                +'<div class="input-group">'
                +'<span class="input-group-addon"> 二级密码:</span>'
                +'<input id="password" type="password" class="layui-input" placeholder="请输入密码">'
                +'</div>'
                +'</div>'
                +'</div>'
            ,
            btn:['确认'],
            btn1: function (index,layero) {
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/userWithdrawCoinAppealMgr/updateStatus", function (d) {
                    if(d.success){
                        Feng.success("审核通过!");
                    }else{
                        Feng.error("操作失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("操作失败!" + data.message + "!");
                });
                var dataStr = {'appealId':data.appealId,"status":2,"password":password};
                ajax.setData(dataStr);
                ajax.start();
                table.reload(withdrawCoinAppeal.tableId);
                layer.close(index);
            }
        });
    };

 // 工具条点击事件
    table.on('tool(' + withdrawCoinAppeal.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'yesStatus') {
        	withdrawCoinAppeal.yesStatus(data);
        } 
        if (layEvent === 'noStatus') {
        	withdrawCoinAppeal.noStatus(data);
        } 
    });
    
});
