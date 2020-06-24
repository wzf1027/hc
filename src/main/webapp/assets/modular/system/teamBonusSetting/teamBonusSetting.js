layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    
    var teamBonusSetting = {
        tableId: "teamBonusSettingTable"    //表格id
    };

    
    teamBonusSetting.openAdd = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '添加',
            content: Feng.ctxPath + '/teamBonusSettingMgr/teamBonusSetting_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(teamBonusSetting.tableId);
            }
        });
    };

    
    /**
     * 初始化表格的列
     */
    teamBonusSetting.initColumn = function () {
        return [[
            {field: 'settingId', hide: true, sort: true, title: 'id'},
            {field: 'levelName', sort: true, title: '等级名称'},
            {field: 'minPrice', sort: true, title: '最小额度'},
            {field: 'maxPrice', sort: true, title: '最大额度'},
            {field: 'bonusRatio', sort: true, title: '返利额度'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + teamBonusSetting.tableId,
        url: Feng.ctxPath + '/teamBonusSettingMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: teamBonusSetting.initColumn()
    });


    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	teamBonusSetting.openAdd();
    });
    
    teamBonusSetting.onEdit= function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            area:["800px","800px"],
            content: Feng.ctxPath + '/teamBonusSettingMgr/teamBonusSetting_update/' + data.settingId,
            end: function () {
                admin.getTempData('formOk') && table.reload(teamBonusSetting.tableId);
            }
        });
    };

    teamBonusSetting.onDelete= function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/teamBonusSettingMgr/delete", function (data) {
                if(data.success){
                	Feng.success("删除成功!");
                }else{
                	 Feng.error("删除失败!" + data.message + "!");
                }
                table.reload(teamBonusSetting.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("settingId", data.settingId);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    };
    
    // 工具条点击事件
    table.on('tool(' + teamBonusSetting.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
        	teamBonusSetting.onEdit(data);
        } else if (layEvent === 'delete') {
        	teamBonusSetting.onDelete(data);
        }
    });

   
});
