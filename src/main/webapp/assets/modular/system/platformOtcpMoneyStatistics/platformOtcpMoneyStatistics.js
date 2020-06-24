layui.use(['layer', 'form', 'table', 'admin', 'ax','laydate'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;

    var platformOtcpMoneyStatistics = {
        tableId: "platformOtcpMoneyStatisticsTable",    //表格id
        condition: {
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
    platformOtcpMoneyStatistics.initColumn = function () {
        return [[
            {field: 'totalSellerNumber', sort: true, title: '会员持有HC(含承兑商)'},
            {field: 'totalMerchant', sort: true, title: '商户持有HC'},
            {field: 'totalAgent', sort: true, title: '代理商持有HC'}
        ]];
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + platformOtcpMoneyStatistics.tableId,
        url: Feng.ctxPath + '/platformOtcpMoneyStatisticsMgr/list',
        page: false,
        height: "full-158",
        cellMinWidth: 100,
        cols: platformOtcpMoneyStatistics.initColumn()
    });
    
    
});
