layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var sellOtcpOrder = {
        tableId: "buyCoinOrderTable",    //表格id
        condition: {
            phone: "",
            timeLimit: ""
        }
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
    sellOtcpOrder.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'orderId', hide: true, sort: true, title: 'orderId'},
            {field: 'serialno', sort: true,title: '会员流水订单号', minWidth: 200},
            {field: 'phone', sort: true,title: '会员账号', minWidth: 160},
            {field: 'sellerIp', sort: true,title: '会员IP', minWidth: 200,templet :
                    function(d) {
                        if(d.sellerIp != null &&  d.sellerIp==d.merchantIp){
                            return '<span style="color:#f94e18;">' + d.sellerIp + '</span>';
                        }else if(d.sellerIp != null){
                            return d.sellerIp;
                        }
                        return  '';
                    }
                    },
            {field: 'sellerCity', sort: true,title: '会员IP所在城市', minWidth: 160},
            {field: 'merchantIp', sort: true,title: '商户IP', minWidth: 200,templet :
                    function(d) {
                        if(d.merchantIp != null && d.sellerIp==d.merchantIp){
                            return '<span style="color:#f94e18;">' + d.merchantIp + '</span>';
                        }else if(d.merchantIp!= null){
                            return d.merchantIp;
                        }
                        return  '';
                    }},
            {field: 'merchantCity', sort: true,title: '商户IP所在城市', minWidth: 160},
            {field: 'buyerAccount', sort: true,title: '商户账号', minWidth: 160},
            {field: 'userOrderNo', sort: true,title: '商户流水订单号', minWidth: 200},
            {field: 'number', sort: true,title: '交易金额'},
            {field: 'agentFeeRatio', sort: true,title: '代理商手续费率'},
            {field: 'agentFee', sort: true,title: '代理商费用'},
            {field: 'feeRatio', sort: true,title: '商户手续费率'},
            {field: 'merchantFee', sort: true,title: '商户费用'},
            {field: 'typeName', sort: true,title: '支付通道'},
            {field: 'statusName', sort: true,title: '状态'},
            {field: 'appealName', sort: true,title: '是否冻结'},
            {field: 'isSuccessName', sort: true,title: '是否回调成功'},
            {field: 'createTime', sort: true, title: '匹配时间', minWidth: 160},
            {field: 'sroCreateTime', sort: true, title: '挂单出售时间', minWidth: 160},
            {field: 'closeTime', sort: true, title: '完成时间', minWidth: 160} ,
            {field: 'orderCodeName', sort: true, title: '订单类型', minWidth: 160} ,
            {field: 'payMethod', sort: true,title: '收款码', minWidth: 250,templet :
            function(d) {
            		if(d.payMethodType<3){
            			return '<div>账号：<span style="color:blue;">'+d.payMethodAccount+
                                '</span></br>姓名：<span style="color:blue;">'+d.payMethodName+
                                '</span></br>昵称：<span style="color:blue;">'+d.payMethodNickName+'</span></div>';
            		}else if(d.payMethodType ==3 || d.payMethodType ==4 || d.payMethodType ==7 || d.payMethodType ==8 ){
                        return '<div>银行卡号：<span style="color:blue;">'+d.payMethodAccount+'</span>' +
                            '</br>姓名：<span style="color:blue;">'+d.payMethodName+'</span>' +
                            '</br>银行名称：<span style="color:blue;">'+d.payMethodCardBankName+'</span>' +
                            '</br>昵称：<span style="color:blue;">'+d.payMethodNickName+'</span></div>';
                    }
            		return '<div>账号：<span style="color:blue;">'+d.payMethodAccount+'</span>' +
                        '</br>姓名：<span style="color:blue;">'+d.payMethodName+'</span>' +
                        '</br>昵称：<span style="color:blue;">'+d.payMethodNickName+'</span></div>';
            	}
            },
            {field: 'remark', sort: true, title: '操作信息', minWidth: 250} ,
            {field: 'dealer', sort: true, title: '操作人', minWidth: 160},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellOtcpOrder.tableId,
        url: Feng.ctxPath + '/buyCoinOrderMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellOtcpOrder.initColumn()
    });
    
    /**
     * 点击查询按钮
     */
    sellOtcpOrder.search = function () {
        var queryData = {};
        queryData['serialno'] = $("#serialno").val();
        queryData['seller'] = $("#seller").val();
        queryData['remark'] = $("#remark").val();
        queryData['userOrderNo'] = $("#userOrderNo").val();
        queryData['account'] = $("#account").val();
        queryData['payMethodAccount'] = $("#payMethodAccount").val();
        queryData['payMethodName'] = $("#payMethodName").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['status'] = $('#status option:selected').val();
        queryData['payMethodType'] = $('#payMethodType option:selected').val();
        queryData['isAppeal'] = $('#isAppeal option:selected').val();
        queryData['isSuccess'] = $('#isSuccess option:selected').val();
        queryData['orderCode'] = $('#orderCode option:selected').val();
        table.reload(sellOtcpOrder.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellOtcpOrder.search();
    });
    
    
    /**
     * 导出excel按钮
     */
    sellOtcpOrder.exportExcel = function () {
        var checkRows = table.checkStatus(sellOtcpOrder.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	sellOtcpOrder.exportExcel();
    });
    sellOtcpOrder.onDelete = function (data) {
        layer.open({
            id:1,
            type: 1,
            title:'是否进行申诉?',
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
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/updateAppeal", function (d) {
                    if(d.success){
                        Feng.success("申请成功!");
                    }else{
                        Feng.error("申请失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("申请失败!" + data.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            }
        });
    };
    
    sellOtcpOrder.onUpdateAppeal= function (data) {
        layer.open({
            id:1,
            type: 1,
            title:'您是否进行申诉审核操作',
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
            btn:['确认收款','拒绝收款'],
            btn1:function (index,layero) {
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/confirmUpdateStatus", function (d) {
                    if(d.success){
                        Feng.success("确认成功!");
                    }else{
                        Feng.error("确认失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("确认失败!" + data.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            },
            btn2:function(){
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/noConfirmUpdateStatus", function (data) {
                    if(data.success){
                        Feng.success("拒绝成功!");
                        layer.closeAll();
                        table.reload(sellOtcpOrder.tableId);
                    }else{
                        Feng.error("拒绝失败!");
                        layer.closeAll();
                        table.reload(sellOtcpOrder.tableId);
                    }

                }, function (data) {
                    Feng.error("拒绝失败!" + data.responseJSON.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            }
            });
        }
    sellOtcpOrder.onFinishStatus = function (data) {
        layer.open({
            id:1,
            type: 1,
            title:'是否进行确认收款',
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
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/finishStatus", function (d) {
                    if(d.success){
                        Feng.success("确认收款成功!");
                    }else{
                        Feng.error("确认收款失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("确认收款失败!" + data.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            }
        });
    };
    sellOtcpOrder.updateOrderStatus = function (data){
        layer.open({
            id:1,
            type: 1,
            title:'确认重新激活订单',
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
            btn:['确认重新激活'],
            btn1: function (index,layero) {
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/updateOrderStatus", function (d) {
                    if(d.success){
                        Feng.success("激活成功!");
                    }else{
                        Feng.error("激活失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("激活失败!" + data.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            }
        });
    }
    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        sellOtcpOrder.openAdd();
    });


    sellOtcpOrder.openAdd = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '补空单',
            content: Feng.ctxPath + '/buyCoinOrderMgr/coinOrder_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(sellOtcpOrder.tableId);
            }
        });
    };


    sellOtcpOrder.updateOrderNumber = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '800px'],
            title: '补单',
            content: Feng.ctxPath + '/buyCoinOrderMgr/update_number/' + data.orderId,
            end: function () {
                admin.getTempData('formOk') && table.reload(sellOtcpOrder.tableId,table.cache);
            }
        });
    };

    sellOtcpOrder.returnBuyCoinOrder = function(data){
        layer.open({
            id:1,
            type: 1,
            title:'确认返补空单操作',
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
            btn:['确认返补单'],
            btn1: function (index,layero) {
                var password = $('#password').val();
                var ajax = new $ax(Feng.ctxPath + "/buyCoinOrderMgr/returnBuyCoinOrder", function (d) {
                    if(d.success){
                        Feng.success("返补成功!");
                    }else{
                        Feng.error("返补失败!" + d.message + "!");
                    }
                }, function (data) {
                    Feng.error("返补失败!" + data.message + "!");
                });
                ajax.set("password", password);
                ajax.set("orderId", data.orderId);
                ajax.start();
                table.reload(sellOtcpOrder.tableId);
                layer.close(index);
            }
        });
    }
    
 // 工具条点击事件
    table.on('tool(' + sellOtcpOrder.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'appealAdd') {
        	sellOtcpOrder.onDelete(data);
        }else if(layEvent === 'updateAppeal'){
        	sellOtcpOrder.onUpdateAppeal(data);
        }else if(layEvent === 'finishStatus'){
        	sellOtcpOrder.onFinishStatus(data);
        }else if(layEvent === 'updateOrderStatus'){
        	sellOtcpOrder.updateOrderStatus(data);
        }else if(layEvent === 'updateOrderNumber'){
        	sellOtcpOrder.updateOrderNumber(data);
        }else if(layEvent ==='returnBuyCoinOrder'){
            sellOtcpOrder.returnBuyCoinOrder(data);
        }
    });
    
    
});
