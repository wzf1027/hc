layui.use(['table', 'admin', 'ax', 'ztree'], function () {
    var $ = layui.$;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    /**
     * 系统管理--参数管理
     */
    var Config = {
        tableId: "configTable",
        condition: {
            configId: ""
        }
    };

    /**
     * 初始化表格的列
     */
    Config.initColumn = function () {
        return [[
            {field: 'rateId', hide: true, sort: true, title: 'id'},
            {field: 'level',  title: '代数'},
            {field: 'type', title: '支付类型',templet:"#tpt2"},
            {field: 'bonusRatio', title: '返利比例',templet:"#tpt1"},
            {field: 'updateTime',  title: '更新时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    /**
     * 点击查询按钮
     */
    Config.search = function () {
        var queryData = {};
        table.reload(Config.tableId, {where: queryData});
    };


    /**
     * 弹出添加
     */
    Config.openAddConfig = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加',
            area:["450px","400px"],
            content: Feng.ctxPath + '/promotionRateMgr/promotion_rate_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(Config.tableId);
            }
        });
    };


    $('#btnAdd').click(function () {
        Config.openAddConfig();
    });

    /**
     * 点击编辑参数
     *
     * @param data 点击按钮时候的行数据
     */
    Config.onEditConfig = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '修改',
            area:["450px","400px"],
            content: Feng.ctxPath + '/promotionRateMgr/promotion_rate_edit?rateId=' + data.rateId,
            end: function () {
                admin.getTempData('formOk') && table.reload(Config.tableId);
            }
        });
    };


    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Config.tableId,
        url: Feng.ctxPath + '/promotionRateMgr/list',
        page: false,
        height: "full-158",
        cellMinWidth: 100,
        limit:100,
        limits: [100,200,300,400,500],
        cols: Config.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        Config.search();
    });


    Config.onDeleteConfig=function(data)
    {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/promotionRateMgr/delete", function (data) {
                if(data.success){
                    Feng.success("删除成功");
                }else{
                    Feng.error("删除失败!" + data.message + "!");
                }
                table.reload(Config.tableId);

            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("rateId", data.rateId);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    }

    // 工具条点击事件
    table.on('tool(' + Config.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            Config.onEditConfig(data);
        } else if (layEvent === 'delete') {
            Config.onDeleteConfig(data);
        }
    });
});
