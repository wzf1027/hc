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
            {field: 'accountCode', sort: true, title: '账号ID', minWidth: 160},
            {field: 'name', sort: true, title: '姓名', minWidth: 160},
            {field: 'authName', sort: true, title: '审核状态'},
            {field: 'status', sort: true, templet:function(d){
            	if(d.status=='ENABLE'){
            		return '正常';
            	}
            	return '冻结';
            }, title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 200},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };


    /**
     * 点击查询按钮
     */
    merchant.search = function () {
        var queryData = {};
        queryData['name'] = $("#name").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(merchant.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + merchant.tableId,
        url: Feng.ctxPath + '/agentMerchantUserMgr/list',
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
    
    
  
    merchant.changeStatus = function (userId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/merchantMgr/freeze", function (data) {
                Feng.success("开启成功!");
            }, function (data) {
                Feng.error("开启失败!");
                table.reload(merchant.tableId);
            });
            ajax.set("userId", userId);
            ajax.start();
        } else {
            var ajax = new $ax(Feng.ctxPath + "/merchantMgr/freeze", function (data) {
                Feng.success("关闭成功!");
            }, function (data) {
                Feng.error("关闭失败!" + data.responseJSON.message + "!");
                table.reload(merchant.tableId);
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
            area:['500px','300px'],
            title: '商户费用',
            content: Feng.ctxPath + '/merchantMgr/payMethodFee/' + data.userId,
            end: function () {
                admin.getTempData('formOk') && table.reload(merchant.tableId);
            }
        });
    };
    
    
    merchant.AgentBonus = function (data) {
    	admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:['500px','300px'],
            title: '代理商费用',
            content: Feng.ctxPath + '/merchantMgr/agentBonus/' + data.userId,
            end: function () {
                admin.getTempData('formOk') && table.reload(merchant.tableId);
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
    				table.reload(merchant.tableId);
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
