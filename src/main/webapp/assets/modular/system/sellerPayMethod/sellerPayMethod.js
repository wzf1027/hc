layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    var sellerPayMethod = {
        tableId: "sellerPayMethodTable",//表格id
        condition: {
            account: "",
            phone: "",
            payMethodName: "",
            payMethodType:"",
            nickName:"",
            isCheck:""
        }
    };

    /**
     * 初始化表格的列
     */
    sellerPayMethod.initColumn = function () {
        return [[
        	{type: 'checkbox'},
            {field: 'payMethodId', hide: true, sort: true, title: 'payMethodId'},
            {field: 'sellerAccount', sort: true, title: '会员账号', minWidth: 150},
            {field: 'phone', sort: true, title: '会员手机号码', minWidth: 150},
            {field: 'typeName', sort: true, title: '收款方式', minWidth: 150},
            {field: 'account', sort: true, title: '支付宝/微信/银行卡号', minWidth: 150},
            {field: 'cardBank', sort: true, title: '开户行', minWidth: 150},
            {field: 'cardBankName', sort: true, title: '所属银行', minWidth: 150},
            {field: 'name', sort: true, title: '真实姓名', minWidth: 150},
            {field: 'qrCode', sort: true, title: '二维码',templet:function (d) {
                    if(d.qrCode){
                        return  '<div onclick="show_img(this)" ><img src="'+d.qrCode+'" ' +
                        'alt="" width="90px" height="50px"></a></div>';
                    }
                    return '';
                }
                },
            {field: 'lineStatus', sort: true, title: '在线状态'},
            {field: 'nickName', sort: true, title: '昵称'},
            {field: 'successRatio', sort: true, title: '成功率（%）', minWidth: 150},
            {field: 'successNumber', sort: true, title: '成功数量'},
            {field: 'todayTotalNumber', sort: true, title: '今日订单数量', minWidth: 150},
            {field: 'successPrice', sort: true, title: '今日成功接单金额', minWidth: 150},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 280}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + sellerPayMethod.tableId,
        url: Feng.ctxPath + '/sellerPayMethodMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: sellerPayMethod.initColumn()
    });

    /**
     * 点击查询按钮
     */
    sellerPayMethod.search = function () {
        sellerPayMethod.condition.account = $("#account").val();
        sellerPayMethod.condition.phone = $("#phone").val();
        sellerPayMethod.condition.payMethodName= $("#payMethodName").val();
        sellerPayMethod.condition.nickName= $("#nickName").val();
        sellerPayMethod.condition.payMethodType = $('#payMethodType option:selected').val();
        sellerPayMethod.condition.isCheck = $('#isCheck option:selected').val();
        table.reload(sellerPayMethod.tableId, {where: sellerPayMethod.condition});
    };

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        sellerPayMethod.search();
    });



    sellerPayMethod.changeStatus = function (payMethodId, checked) {
        if (checked) {
            var ajax = new $ax(Feng.ctxPath + "/sellerPayMethodMgr/isSoldOut", function (data) {
                Feng.success("封禁成功!");
            }, function (data) {
                Feng.error("封禁失败!" + data.message + "!");
            });
            ajax.set("payMethodId", payMethodId);
            ajax.set("status", 1);
            ajax.start();
            table.reload(sellerPayMethod.tableId);
        } else {
            var ajax = new $ax(Feng.ctxPath + "/sellerPayMethodMgr/isSoldOut", function (data) {
                Feng.success("解禁成功!");
            }, function (data) {
                Feng.error("解禁失败!" + data.responseJSON.message + "!");
            });
            ajax.set("payMethodId", payMethodId);
            ajax.set("status", 2);
            ajax.start();
            table.reload(sellerPayMethod.tableId);
        }
    };

    sellerPayMethod.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/sellerPayMethodMgr/delete", function (data) {
                if(data.success){
                    Feng.success("删除成功!");
                }else{
                    Feng.error("删除失败!" + data.message + "!");
                }
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("payMethodId", data.payMethodId);
            ajax.start();
            table.reload(sellerPayMethod.tableId);
        };
        Feng.confirm("是否删除?", operation);
    };

    // 工具条点击事件
    table.on('tool(' + sellerPayMethod.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'frozen') {
            sellerPayMethod.changeStatus(data.payMethodId,true);
        }  else if (layEvent === 'unFrozen') {
            sellerPayMethod.changeStatus(data.payMethodId,false);
        }else if (layEvent === 'delete') {
            sellerPayMethod.onDelete(data);
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