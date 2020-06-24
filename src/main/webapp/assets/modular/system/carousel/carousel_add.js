layui.use(['layer', 'form', 'admin', 'ax','upload','layedit'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var upload = layui.upload;
    var admin = layui.admin;
    var layer = layui.layer;
    var layedit = layui.layedit;
    // 让当前iframe弹层高度适应
    admin.iframeAuto();
  //上传图片,必须放在 创建一个编辑器前面
	layedit.set({
        uploadImage: {
            url: Feng.ctxPath +'/image/uploadImg', //接口url
            type: 'post' //默认post
        }
    });
 
    //创建一个编辑器  id=LAY_demo_editor
    var editIndex = layedit.build('content');
    form.verify({
        content: function(value) { 
             return layedit.sync(editIndex);
            }
    });
    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/carouselMgr/add", function (data) {
            Feng.success("添加成功！");

            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);

            //关掉对话框
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("添加失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
    

    var uploadInst =  upload.render({
          elem: '#imageUrl',
          url: Feng.ctxPath +'/image/addPhoto', // 上传接口
          before: function(obj){
              //预读本地文件示例，不支持ie8
              obj.preview(function(index, file, result){
                $('#imageSrc').attr('src', result); //图片链接（base64）
                
              });
            },
          done: function (res) {
          	 //如果上传失败
              if(res.code !=200){
                return layer.msg('上传失败');
              }
              //上传成功
              $('#carouselImage').val(res.data.src);
          },
          error: function () {
          	//演示失败状态，并实现重传
              var demoText = $('#imageTest');
              demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
              demoText.find('.demo-reload').on('click', function(){
                uploadInst.upload();
              })
          }
    });
});