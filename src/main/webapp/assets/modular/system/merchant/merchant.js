layui.use(['layer', 'form', 'table', 'ztree', 'laydate', 'admin', 'ax'], function () {
	 var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var merchant = {
        tableId: "merchantTable",    //表格id
        condition: {
            name: "",
            deptId: "",
            timeLimit: ""
        }
    };

    /**
     * 初始化表格的列
     */
    merchant.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'accountCode', sort: true, title: '用户ID', minWidth: 160},
            {field: 'account', sort: true, title: '登录账号', minWidth: 160},
            {field: 'name', sort: true, title: '姓名', minWidth: 160},
            {field: 'recommendPhone', sort: true, title: '推荐人'},
            {field: 'authName', sort: true, title: '审核状态'},
            {field: 'status', sort: true, templet: '#statusTpl', title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 250},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };


    /**
     * 点击查询按钮
     */
    merchant.search = function () {
        var queryData = {};
        queryData['phone'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['recommend'] = $("#recommend").val();
        queryData['isAuth'] = $('#isAuth option:selected') .val();
        queryData['enabled'] = $('#enabled option:selected') .val();
        table.reload(merchant.tableId, {where: queryData},table.cache);
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + merchant.tableId,
        url: Feng.ctxPath + '/merchantMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: merchant.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#timeLimit',
        range: true,
        type: 'datetime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	merchant.search();
    });
    
    /**
     * 导出excel按钮
     */
    merchant.exportExcel = function () {
        var checkRows = table.checkStatus(merchant.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	merchant.exportExcel();
    });

    
    merchant.AuthDetail = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', '800px'],
            title: '实名认证信息',
            content: Feng.ctxPath + '/merchantMgr/authPage/' + data.userId,
            end: function () {
                admin.getTempData('formOk') && table.reload(merchant.tableId,table.cache);
            }
        });
    };
    
    
    merchant.dealAuthStatus = function (data,msg) {
    	var ajax = new $ax(Feng.ctxPath + "/merchantMgr/updateAuth",function (data) {
    	      table.reload(merchant.tableId,table.cache);
            Feng.success(msg);
            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);
            admin.closeThisDialog();
        }, function (data) {
            table.reload(merchant.tableId,table.cache);
            Feng.error("操作失败!" + data.responseJSON.message + "!");
        });
        ajax.setData(data);
        ajax.start();
  
    };
    
    $('#agree').click(function () {
    	var userId = $("#authForm input[name='userId']").val();
    	var data ={'userId':userId,'status':1};
    	merchant.dealAuthStatus(data,'审核通过成功');
    });
    
    $('#refuse').click(function () {
    	var userId = $("#authForm input[name='userId']").val();
    	var data ={'userId':userId,'status':-1};
    	merchant.dealAuthStatus(data,"审核不通过成功");
    });
    
    merchant.changeStatus = function (userId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/merchantMgr/freeze", function (data) {
                table.reload(merchant.tableId,table.cache);
                Feng.success("开启成功!");
            }, function (data) {
                table.reload(merchant.tableId,table.cache);
                Feng.error("开启失败!");
            });
            ajax.set("userId", userId);
            ajax.start();
        } else {
            var ajax = new $ax(Feng.ctxPath + "/merchantMgr/freeze", function (data) {
                table.reload(merchant.tableId,table.cache);
                Feng.success("关闭成功!");
            }, function (data) {
                table.reload(merchant.tableId,table.cache);
                Feng.error("关闭失败!" + data.responseJSON.message + "!");
            });
            ajax.set("userId", userId);
            ajax.start();
        }
    };
    
    form.on('switch(status)', function (obj) {
        var userId = obj.elem.value;
        var checked = obj.elem.checked ? true : false;
        merchant.changeStatus(userId, checked);
    });
    
    merchant.PayMethodFee = function (data) {
    	admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', 'auto'],
            title: '商户费用设置',
            content: Feng.ctxPath + '/merchantMgr/payMethodFee_update/' + data.userId,
            end: function () {
                admin.getTempData('formOk') &&  table.reload(merchant.tableId,table.cache);;
            }
        });
    };
    
    
    merchant.AgentBonus = function (data) {
    	admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area: ['800px', 'auto'],
            title: '代理商费用设置',
            content: Feng.ctxPath + '/merchantMgr/gentBonus_update/' + data.userId,
            end: function () {
                admin.getTempData('formOk') &&   table.reload(merchant.tableId,table.cache);;
            }
        });
    };
    
    merchant.openWallter = function openWallter(sellerId,type) {
    	layui.use(['table', 'form'], function() {
    		table2 = layui.table;
    		var form = layui.form;
    		layer.open({
    			type: 1,
    			title: '钱包明细',
    			area: ['500px', '200px'], //宽高
    			content: $('#openProductBox'),
    			end: function() {
    	             table.reload(merchant.tableId,table.cache);
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
    			        height: "117px",
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
    						}]
    					]
    				});
    			}
    		});
    	});
    }
    
 // 工具条点击事件
    table.on('tool(' + merchant.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'authDetail') {
        	merchant.AuthDetail(data);
        }else if (layEvent === 'merchantPayMethodFee') {
        	merchant.PayMethodFee(data);
        }else if (layEvent === 'agentBonus') {
        	merchant.AgentBonus(data);
        }else if(layEvent === 'merchantWallter'){
        	  merchant.openWallter(data.userId,2);
        }
    });
    
});
