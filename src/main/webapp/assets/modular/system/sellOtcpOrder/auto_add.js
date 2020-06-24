layui.use(['layer', 'form', 'admin', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var layer = layui.layer;
    
    var htmls = '<option value="">请选择</option>'; //全局变量
    $.ajax({
       url: Feng.ctxPath + '/mgr/getPayMethodList',
       type: "post",
       dataType : "json",
       contentType : "application/json",
       async: false,//这得注意是同步
       success: function (result) { 
            resultData = result.data;
            for(var x in resultData){
	 			htmls += '<option value = "' + resultData[x].payMethodId + '">' + resultData[x].account + '</option>';
             } 
              $("#payMehtod").html(htmls);
            }
         });     
       form.render('select');//需要渲染一下

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/sellOtcpOrderMgr/autoAdd", function (data) {
            if(data.success){
            	Feng.success("出售成功！");
            	//传给上个页面，刷新table用
                admin.putTempData('formOk', true);

                //关掉对话框
                admin.closeThisDialog();
            }else{
            	 Feng.error("出售失败！" + data.message);
            }
        

            
        }, function (data) {
            Feng.error("出售失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
});