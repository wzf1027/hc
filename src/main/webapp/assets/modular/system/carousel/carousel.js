layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;
    
    var Carousel = {
        tableId: "carouselTable"    //表格id
    };

    
    Carousel.openAddCarousel = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            area:["800px","800px"],
            title: '添加',
            content: Feng.ctxPath + '/carouselMgr/carousel_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(Carousel.tableId);
            }
        });
    };

    
    /**
     * 初始化表格的列
     */
    Carousel.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'carouselId', hide: true, sort: true, title: 'id'},
            {field: 'carouselName', sort: true, title: '名称'},
            {field: 'image', sort: true, title: '图片',templet:
            	'<div><img src="'+Feng.ctxPath+'{{d.image}}"></div>'
            },
            {field: 'href', sort: true, title: '外链链接'},
            {field: 'sort', sort: true, title: '排序'},
            {field: 'createTime', sort: true, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Carousel.tableId,
        url: Feng.ctxPath + '/carouselMgr/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Carousel.initColumn()
    });


    // 添加按钮点击事件
    $('#btnAdd').click(function () {
    	Carousel.openAddCarousel();
    });
    
    Carousel.onEditCarousel = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑',
            area:["800px","800px"],
            content: Feng.ctxPath + '/carouselMgr/carousel_update/' + data.carouselId,
            end: function () {
                admin.getTempData('formOk') && table.reload(Carousel.tableId);
            }
        });
    };

    Carousel.onDeleteCarousel = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/carouselMgr/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(Carousel.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("carouselId", data.carouselId);
            ajax.start();
        };
        Feng.confirm("是否删除?", operation);
    };
    
    // 工具条点击事件
    table.on('tool(' + Carousel.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
        	Carousel.onEditCarousel(data);
        } else if (layEvent === 'delete') {
        	Carousel.onDeleteCarousel(data);
        }
    });
});
