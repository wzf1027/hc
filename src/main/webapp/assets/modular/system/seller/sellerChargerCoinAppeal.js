layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var sellerChargerCoinAppeal = {
        tableId: "sellerChargerCoinAppealTable"    //表格id
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
    sellerChargerCoinAppeal.search = function () {
        var queryData = {};
        queryData['phone'] = $("#phone").val();
        queryData['timeLimit'] = $("#timeLimit").val();
        queryData['serialno'] = $("#serialno").val();
        queryData['status'] = $('#status option:selected').val();
        table.reload(sellerChargerCoinAppeal.tableId, {where: queryData});
    };
    
 // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	sellerChargerCoinAppeal.search();
    });
    
    /**
     * 导出excel按钮
     */
    sellerChargerCoinAppeal.exportExcel = function () {
        var checkRows = table.checkStatus(sellerChargerCoinAppeal.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };
    
 // 导出excel
    $('#btnExp').click(function () {
    	sellerChargerCoinAppeal.exportExcel();
    });


    /**
     * 初始化表格的列
     */
    sellerChargerCoinAppeal.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'appealId', hide: true, sort: true, title: 'id'},
            {field: 'serialno', sort: true, title: '流水号', minWidth: 200},
            {field: 'phone', sort: true, title: '账号', minWidth: 160},
            {field: 'number', sort: true, title: '充值数量'},
            {field: 'hashValue', title: '凭证',templet:function(d) {
                    return '<div onclick="show_img(this)" ><img src="'+d.hashValue+'" ' +
                        'alt="" width="90px" height="50px"></a></div>';
                }
            },
            {field: 'statusName', sort: true,  title: '状态'},
            {field: 'createTime', sort: true, title: '创建时间', minWidth: 180},
            {field: 'userAccount', sort: true, title: '操作人'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerChargerCoinAppeal.tableId,
        url: Feng.ctxPath + '/sellerChargerCoinAppealMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerChargerCoinAppeal.initColumn()
    });
    
    sellerChargerCoinAppeal.yesStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerChargerCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核通过!");
                table.reload(sellerChargerCoinAppeal.tableId);
            }, function (data) {
                Feng.error("操作失败!" + data.responseJSON.message + "!");
            });
            var dataStr = {'appealId':data.appealId,"status":1}
            ajax.setData(dataStr);            
            ajax.start();
        };
        Feng.confirm("是否确认审核通过?", operation);
    };
    
    sellerChargerCoinAppeal.noStatus = function (data) {
    	var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerChargerCoinAppealMgr/updateStatus", function (data) {
                Feng.success("审核不通过!");
                table.reload(sellerChargerCoinAppeal.tableId);
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
    table.on('tool(' + sellerChargerCoinAppeal.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'yesStatus') {
        	sellerChargerCoinAppeal.yesStatus(data);
        } 
        if (layEvent === 'noStatus') {
        	sellerChargerCoinAppeal.noStatus(data);
        } 
    });
});
//显示大图片
function show_img(t) {
    var t = $(t).find("img");
    //页面层
    layer.open({
        type: 1,
        title:'凭证',
        skin: 'layui-layer-rim', //加上边框
        area: ['500px', '500px'], //宽高 t.width() t.height()
        shadeClose: true, //开启遮罩关闭
        end: function (index, layero) {
            return false;
        },
        content: '<div style="text-align:center"><img src="' + $(t).attr('src') + '" style="width: 50%;height: 50%" /></div>'
    });
}