layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    
    var table2;
    var seller = {
        tableId: "sellerTable",    //表格id
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
    seller.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'sellerId', hide: true, sort: true, title: 'sellerId'},
            {field: 'account', sort: true, title: '账号ID'},
            {field: 'phone', sort: true, title: '手机号码'},
            {field: 'nickName', sort: true, title: '昵称'},
            {field: 'account', sort: true, title: '邀请码'},
            {field: 'refereePhone', title: '推荐人'},
            {field: 'authStatus', sort: true, title: '认证状态'},
            {field: 'bingGoogleName', sort: true, title: '是否绑定谷歌'},
            {field: 'isAccepter', sort: true, title: '是否承兑商'},
            {field: 'enabled', sort: true, templet: '#enabledTpl', title: '用户封禁状态'},
            {field: 'gradEnabled', sort: true, templet: '#gradEnabledTpl', title: '用户封禁接单'},
            {field: 'sellEnabled', sort: true, templet: '#sellEnabledTpl', title: '用户封禁出售'},
            {field: 'tranferEnabled', sort: true, templet: '#tranferEnabledTpl', title: '用户封禁划转'},
            {field: 'buyEnabled', sort: true, templet: '#buyEnabledTpl', title: '用户封禁购买'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 250},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 600,fixed:'right'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + seller.tableId,
        url: Feng.ctxPath + '/sellerMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 150,
        cols: seller.initColumn()
    });


    seller.selletTime = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '提币/划转/出售时间设置',
            content: Feng.ctxPath + '/sellerMgr/seller_time',
            end: function () {
                admin.getTempData('formOk') && table.reload(seller.tableId);
            }
        });
    };


    $('#btnSellerTime').click(function () {
        seller.selletTime();
    });

    /**
     * 点击查询按钮
     */
    seller.search = function () {
        var queryData = {};
        queryData['condition'] = $("#account").val();
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['recommend'] = $("#recommend").val();
        queryData['isAccepter'] = $('#isAccepter option:selected') .val();
        queryData['isAuth'] = $('#isAuth option:selected') .val();
        queryData['enabled'] = $('#enabled option:selected') .val();
        table.reload(seller.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	seller.search();
    });
    
    seller.AuthDetail = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '800px'],
            title: '实名认证信息',
            content: Feng.ctxPath + '/sellerMgr/authPage/' + data.sellerId,
            end: function () {
                admin.getTempData('formOk') && table.reload(seller.tableId);
            }
        });
    };

    seller.authDetailPage = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '800px'],
            title: '实名认证信息',
            content: Feng.ctxPath + '/sellerMgr/authDetailPage/' + data.sellerId,
            end: function () {
                admin.getTempData('formOk') && table.reload(seller.tableId);
            }
        });
    };

    /**
     * 导出excel按钮
     */
    seller.exportExcel = function () {
        var checkRows = table.checkStatus(seller.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	seller.exportExcel();
    });




    seller.googleUnFrozen = function (sellerId) {
        var successMsg = '解绑成功!';
        var failMsg = '解绑失败!';
        var ajax = new $ax(Feng.ctxPath + "/sellerMgr/bingGoogleUnFreeze", function (data) {
            Feng.success(successMsg);
        }, function (data) {
            Feng.error(failMsg+ data.responseJSON.message + "!");
        });
        ajax.set("sellerId", sellerId);
        ajax.start();
        table.reload(seller.tableId);
    };
   
    
    $('#agree').click(function () {
    	var sellerId = $("#sellerAuthForm input[name='sellerId']").val();
    	var data ={'sellerId':sellerId,'status':1};
    	seller.dealAuthStatus(data,'审核通过');
    });
    
    $('#refuse').click(function () {
    	var sellerId = $("#sellerAuthForm input[name='sellerId']").val();
    	var data ={'sellerId':sellerId,'status':-1};
    	seller.dealAuthStatus(data,'审核不通过');
    });
    
    seller.changeStatus = function (sellerId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/sellerMgr/freeze", function (data) {
                Feng.success("开启成功!");
            }, function (data) {
                Feng.error("开启失败!");
            });
            ajax.set("sellerId", sellerId);
            ajax.start();
            table.reload(seller.tableId);
        } else {
            var ajax = new $ax(Feng.ctxPath + "/sellerMgr/freeze", function (data) {
                Feng.success("关闭成功!");
            }, function (data) {
                Feng.error("关闭失败!" + data.responseJSON.message + "!");
            });
            ajax.set("sellerId", sellerId);
            ajax.start();
            table.reload(seller.tableId);
        }
    };
    
    seller.dealAuthStatus = function (data,msg) {
    	var ajax = new $ax(Feng.ctxPath + "/sellerMgr/updateAuth",function (data) {
            Feng.success(msg);
            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("操作失败!" + data.responseJSON.message + "!");
        });
        ajax.setData(data);
        ajax.start();
        table.reload(seller.tableId);
    };
    form.on('switch(enabled)', function (obj) {
        var sellerId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        seller.changeStatus(sellerId, checked);
    });



    seller.gradEnabled = function (sellerId, checked) {
        var successMsg = '关闭成功!';
        var failMsg = '关闭失败!';
        if(checked){
            successMsg = '开启成功!';
            failMsg = '开启失败!';
        }
        var ajax = new $ax(Feng.ctxPath + "/sellerMgr/gradEnabledFreeze", function (data) {
            Feng.success(successMsg);
        }, function (data) {
            Feng.error(failMsg+ data.responseJSON.message + "!");
        });
        ajax.set("sellerId", sellerId);
        ajax.start();
        table.reload(seller.tableId);
    };

    form.on('switch(gradEnabled)', function (obj) {
        var sellerId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        seller.gradEnabled(sellerId, checked);
    });


    seller.sellEnabled = function (sellerId, checked) {
        var successMsg = '关闭成功!';
        var failMsg = '关闭失败!';
        if(checked){
            successMsg = '开启成功!';
            failMsg = '开启失败!';
        }
        var ajax = new $ax(Feng.ctxPath + "/sellerMgr/sellEnabledFreeze", function (data) {
            Feng.success(successMsg);
        }, function (data) {
            Feng.error(failMsg+ data.responseJSON.message + "!");
        });
        ajax.set("sellerId", sellerId);
        ajax.start();
        table.reload(seller.tableId);
    };
    form.on('switch(sellEnabled)', function (obj) {
        var sellerId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        seller.sellEnabled(sellerId, checked);
    });


    seller.tranferEnabled = function (sellerId, checked) {
        var successMsg = '关闭成功!';
        var failMsg = '关闭失败!';
        if(checked){
            successMsg = '开启成功!';
            failMsg = '开启失败!';
        }
        var ajax = new $ax(Feng.ctxPath + "/sellerMgr/tranferEnabledFreeze", function (data) {
            Feng.success(successMsg);
        }, function (data) {
            Feng.error(failMsg+ data.responseJSON.message + "!");
        });
        ajax.set("sellerId", sellerId);
        ajax.start();
        table.reload(seller.tableId);
    };

    form.on('switch(tranferEnabled)', function (obj) {
        var sellerId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        seller.tranferEnabled(sellerId, checked);
    });

    seller.buyEnabled = function (sellerId, checked) {
        var successMsg = '关闭成功!';
        var failMsg = '关闭失败!';
        if(checked){
            successMsg = '开启成功!';
            failMsg = '开启失败!';
        }
        var ajax = new $ax(Feng.ctxPath + "/sellerMgr/buyEnabledFreeze", function (data) {
            Feng.success(successMsg);
        }, function (data) {
            Feng.error(failMsg+ data.responseJSON.message + "!");
        });
        ajax.set("sellerId", sellerId);
        ajax.start();
        table.reload(seller.tableId);
    };

    form.on('switch(buyEnabled)', function (obj) {
        var sellerId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        seller.buyEnabled(sellerId, checked);
    });


    seller.openWallter = function (sellerId,type) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '800px'],
            title: '钱包明细',
            content: Feng.ctxPath + '/sellerMgr/wallterPage/' + data.sellerId,
            end: function () {
                admin.getTempData('formOk') && table.reload(seller.tableId);
            }
        });
    };
    
    seller.openWallter = function openWallter(sellerId,type) {
    	layui.use(['table', 'form'], function() {
    		table2 = layui.table;
    		var form = layui.form;
    		layer.open({
    			type: 1,
    			title: '钱包明细',
    			area: ['500px', '200px'], //宽高
    			content: $('#openProductBox'),
    			end: function() {
    				table.reload(seller.tableId);
    			},
    			success: function() {
    				table2.render({
    					elem: '#openProductTable',
    					id: 'openProductTable',
    					method: 'post', //接口http请求类型，默认：get
    					url: Feng.ctxPath + '/sellerMgr/wallterList', //?page=1&limit=10（该参数可通过 request 自定义）
    					where: {
    						sellerId: sellerId,
    						type: type
    					}, 
    					page: false,
    					height: "full-158",
    			        cellMinWidth: 100,
    					cols: [
    						[{
    							field: 'availableBalance', //字段名
    							title: '可用余额', //标题
    							sort: true //是否允许排序 默认：false
    						}, {
    							field: 'frozenBalance', //字段名
    							title: '冻结余额', //标题
    							sort: true //是否允许排序 默认：false
    						}, {
    							field: 'code', //字段名
    							title: '类别', //标题
    							sort: true //是否允许排序 默认：false
    						},
    			            {align: 'center', toolbar: '#tableBar2', title: '操作', minWidth: 280}
    						]
    					]
    				});
    			}
    		});
    	});
    }



    seller.memberProfitWallter = function memberProfitWallter(sellerId,type) {
        layui.use(['table', 'form'], function() {
            table2 = layui.table;
            var form = layui.form;
            layer.open({
                type: 1,
                title: '钱包明细',
                area: ['500px', '200px'], //宽高
                content: $('#openProductBox'),
                end: function() {
                    table.reload(seller.tableId);
                },
                success: function() {
                    table2.render({
                        elem: '#openProductTable',
                        id: 'openProductTable',
                        method: 'post', //接口http请求类型，默认：get
                        url: Feng.ctxPath + '/sellerMgr/wallterList', //?page=1&limit=10（该参数可通过 request 自定义）
                        where: {
                            sellerId: sellerId,
                            type: type
                        },
                        page: false,
                        height: "full-158",
                        cellMinWidth: 100,
                        cols: [
                            [{
                                field: 'availableBalance', //字段名
                                title: '可用余额', //标题
                                sort: true //是否允许排序 默认：false
                            }, {
                                field: 'frozenBalance', //字段名
                                title: '冻结余额', //标题
                                sort: true //是否允许排序 默认：false
                            }, {
                                field: 'code', //字段名
                                title: '类别', //标题
                                sort: true //是否允许排序 默认：false
                            }
                            ]
                        ]
                    });
                }
            });
        });
    }


    seller.updatePassword = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerMgr/updatePassword", function (d) {
                table.reload(seller.tableId);
                Feng.success("重置成功!");
            }, function (data) {
                Feng.error("重置失败!" + data.responseJSON.message + "!");
            });
            ajax.set("sellerId", data.sellerId);
            ajax.start();
        };
        Feng.confirm("是否重置登录密码/交易密码，默认密码为123456?", operation);
    };


    // 工具条点击事件
    table.on('tool(' + seller.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'authDetail') {
        	seller.AuthDetail(data);
        }else if(layEvent === 'authDetailPage'){
            seller.authDetailPage(data);
        }else if(layEvent === 'accepterWallter'){
        	seller.memberProfitWallter(data.userId,2);
        }else if(layEvent === 'memberWallter'){
        	seller.openWallter(data.sellerId,1);
        }else if(layEvent === 'memberProfitWallter'){
        	seller.memberProfitWallter(data.sellerId,3);
        }else if(layEvent === 'googleUnFrozen'){
            seller.googleUnFrozen(data.sellerId);
        }else  if(layEvent ===  "updatePassword"){
            seller.updatePassword(data);
        }
    });
    
    seller.updateMoney = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '500px'],
            title: '修改余额',
            content: Feng.ctxPath + '/sellerMgr/updteWallter_Number/' + data.sellerWallterId,
            end: function () {
                admin.getTempData('formOk') && table.reload('openProductTable');
            }
        });
    };    
    
    table.on('tool(openProductTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'updateMoney') {
        	seller.updateMoney(data);
        }
    });
   

    
});
