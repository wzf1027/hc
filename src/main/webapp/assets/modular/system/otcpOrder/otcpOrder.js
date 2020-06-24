layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var otcpOrder = {
        tableId: "otcpOrderTable"    //表格id
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
    otcpOrder.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'orderId', hide: true, sort: true, title: 'orderId'},
            {field: 'serialNo', sort: true, title: '订单流水号', minWidth: 200},
            {field: 'buyerPhone', sort: true, title: '买家账号',minWidth: 160},
            {field: 'sellerPhone', sort: true, title: '卖家账号',minWidth: 160},
            {field: 'price', sort: true, title: '单价'},
            {field: 'number', sort: true, title: '数量'},
            {field: 'remark', sort: true, title: '参考号'},
            {field: 'payMethodTypeName', sort: true, title: '买家支付的类型'},
            {field: 'statusName', sort: true, title: '状态'},
            {field: 'sellerSerialno', title: '出售订单流水号', minWidth: 200},
            {field: 'createTime', sort: true, title: '下单时间', minWidth: 200},
            {field: 'payMethodIds', sort: true, title: '收款方式',templet:function(d){
            	if(d.payMethodIds){
            		var arr =JSON.parse(d.payMethodIds);
            		console.log(d.payMethodIds);
            		var html="<div>";
            		for(var i=0;i<arr.length;i++){
            			
            			if(arr[i].type ==1){
            				html += '<a class="layui-btn  layui-btn-xs" id="aplDetail" data-type="'+d.sellerType+'"  data-value="'+arr[i].id+'" >支付宝</a>';
            			}
            			if(arr[i].type ==2){
            				html += '<a class="layui-btn  layui-btn-xs" id="wxDetail" data-type="'+d.sellerType+'" data-value="'+arr[i].id+'">微信</a>';
            			}
            			if(arr[i].type ==3){
            				html += '<a class="layui-btn  layui-btn-xs" id="cardDetail" data-type="'+d.sellerType+'" data-value="'+arr[i].id+'" >银行卡</a>';
            			}
            		}
            	}
            	html += '</div>';
            	return html ;
            }, minWidth: 280},
            {field: 'appealerRoles', hide: true, sort: true, title: '申诉人', minWidth: 160},
            {field: 'appealer', sort: true, title: '申诉人手机号码', minWidth: 160},
            {field: 'appealName', sort: true, title: '申诉状态'},
            {field: 'appealContent', sort: true, title: '申诉内容'},
            {field: 'certificate', title: '图片',width: 130 , align: 'center',templet:function(d){
            	if(d.certificate){
            		return '<div onclick="show_img(this)"><img style="height:100px;width:100px;" src="'+d.certificate+'"></div>';
            	}
            		return '';
            	}
            },
            {field: 'userAccount', sort: true, title: '操作人', minWidth: 160},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
            ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + otcpOrder.tableId,
        url: Feng.ctxPath + '/otcpOrderMgr/list',
        page: true,
        height: "full-180",
        cellMinWidth: 100,
        cols: otcpOrder.initColumn()
    });
    
    /**
     * 导出excel按钮
     */
    otcpOrder.exportExcel = function () {
        var checkRows = table.checkStatus(otcpOrder.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
        	var data = checkRows.data;
        	console.log(data);
        	var arr =[];
            for(var i=0;i<data.length;i++){
            	var obj = data[i];
            	delete obj.buySellType
            	delete obj.buyerId
            	delete obj.orderId
            	delete obj.payMethodId
            	delete obj.payMethodIds
            	delete obj.payMethodType
            	delete obj.type
            	delete obj.certificate
            	arr.push(obj);
            }
            table.exportFile(tableResult.config.id, arr, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	otcpOrder.exportExcel();
    });

    
    /**
     * 
     * 点击查询按钮
     */
    otcpOrder.search = function () {
        var queryData = {};
        queryData['buyerPhone'] = $("#buyerPhone").val();
        queryData['sellerPhone'] = $("#sellerPhone").val();
        queryData['serialno'] = $("#serialno").val();
        queryData['remark'] = $("#remark").val();
        queryData['status'] = $('#status option:selected').val();
        queryData['isAppeal'] = $('#isAppeal option:selected').val();
        queryData['payMethodType'] = $('#payMethodType option:selected').val();
        queryData['timeLimit'] = $("#timeLimit").val();
        table.reload(otcpOrder.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	otcpOrder.search();
    });
    
    otcpOrder.onUpdateAppeal= function (data) {
        layer.confirm('您是否进行申诉审核操作？', {
        	  btn: ['买家胜','卖家胜'] //按钮
        	}, function(){
        		 var ajax = new $ax(Feng.ctxPath + "/otcpOrderMgr/buyerWarn", function (data) {
                     if(data.success){
                    	 Feng.success("审核成功!");
                    	 layer.closeAll();
                         table.reload(otcpOrder.tableId);
                     }else{
                    	 Feng.error("审核失败!");
                    	 layer.closeAll();
                         table.reload(otcpOrder.tableId);
                     }
                    
                 }, function (data) {
                     Feng.error("审核失败!" + data.responseJSON.message + "!");
                 });
                 ajax.set("id", data.orderId);
                 ajax.start();
        	}, function(){
        		 var ajax = new $ax(Feng.ctxPath + "/otcpOrderMgr/sellerWarn", function (data) {
                     if(data.success){
                    	 Feng.success("审核成功!");
                    	 layer.closeAll();
                         table.reload(otcpOrder.tableId);
                     }else{
                    	 Feng.error("审核失败!");
                    	 layer.closeAll();
                         table.reload(otcpOrder.tableId);
                     }
                    
                 }, function (data) {
                     Feng.error("审核失败!" + data.responseJSON.message + "!");
                 });
                 ajax.set("id", data.orderId);
                 ajax.start();
        	});
        }
    
    var isFirst = true;
    otcpOrder.onCannelTrade = function (data) {
        var operation = function () {
        	if(isFirst){
          	  	isFirst = false;
            var ajax = new $ax(Feng.ctxPath + "/otcpOrderMgr/cannelTrade", function (data) {
            	table.reload(otcpOrder.tableId);
            	if(data.success){
            	   Feng.success("取消成功!");
               }else{
            	   Feng.error("取消失败!" + data.message + "!");
               }   
            	isFirst =true
            }, function (data) {
                Feng.error("取消失败!" + data.responseJSON.message + "!");
                isFirst =true
            });
            ajax.set("orderId", data.orderId);
            ajax.start();
        }
        };
        Feng.confirm("是否取消交易?", operation);
    };
    
 
    
    
    
    var isGet=true;
    otcpOrder.onGetMoney = function (data) {
        var operation = function () {
        	if(isGet)
        	{
        		 var ajax = new $ax(Feng.ctxPath + "/otcpOrderMgr/getMoney", function (data) {
                 	table.reload(otcpOrder.tableId);
                 	if(data.success){
                 	   Feng.success("确认收款成功!");
                    }else{
                 	   Feng.error("确认收款失败!" + data.message + "!");
                    }  
                 	isFirst =true
                 }, function (data) {
                     Feng.error("确认收款失败!" + data.responseJSON.message + "!");
                     isFirst =true
                 });
                 ajax.set("orderId", data.orderId);
                 ajax.start();
        	}
           
        };
        Feng.confirm("是否确认收款?", operation);
    };
    
    otcpOrder.onAddAppeal = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '申诉',
            content: Feng.ctxPath + '/otcpOrderMgr/appeal_add/' + data.orderId,
            end: function () {
                admin.getTempData('formOk') && table.reload(otcpOrder.tableId);
            }
        });
    };

    // 工具条点击事件
    table.on('tool(' + otcpOrder.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'updateAppeal') {
        	otcpOrder.onUpdateAppeal(data);
        }else if(layEvent === 'cannelTrade'){
        	otcpOrder.onCannelTrade(data);
        }else if(layEvent === 'getMoney'){
        	otcpOrder.onGetMoney(data);
        }else if(layEvent === 'appealAdd'){
        	otcpOrder.onAddAppeal(data);
        }
    });
    
    otcpOrder.payMethod = function (id,type) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '收款方式信息',
            content: Feng.ctxPath + '/otcpOrderMgr/payMethod?type=' + type+'&id='+id,
            end: function () {
                admin.getTempData('formOk') && table.reload(seller.tableId);
            }
        });
    };

    $(document).on('click','#cardDetail',function(){
    	var id = $(this).attr('data-value');
    	var type = $(this).attr('data-type');
    	otcpOrder.payMethod(id,type);
    });
 
    $(document).on('click','#wxDetail',function(){
    	var id = $(this).attr('data-value');
    	var type = $(this).attr('data-type');
    	otcpOrder.payMethod(id,type);
    });
 
    $(document).on('click','#aplDetail',function(){
    	var id = $(this).attr('data-value');
    	var type = $(this).attr('data-type');
    	otcpOrder.payMethod(id,type);
    });
    
    
});

function show_img(t) {
    var t = $(t).find("img");
    //页面层
    layer.open({
        type: 1,
        skin: 'layui-layer-rim', //加上边框
        area: ['80%', '80%'], //宽高 t.width() t.height() 
        shadeClose: true, //开启遮罩关闭
        end: function (index, layero) {
            return false;
        },
        content: '<div style="text-align:center"><img src="' + $(t).attr('src') + '" style="height:80%;width:80%;" /></div>'
    });
}