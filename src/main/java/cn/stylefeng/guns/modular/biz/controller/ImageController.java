package cn.stylefeng.guns.modular.biz.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cn.stylefeng.guns.core.util.FileEntity;
import cn.stylefeng.guns.core.util.FileUploadTool;
import cn.stylefeng.guns.core.util.ImageUtils;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@CrossOrigin
@RestController
@RequestMapping({"/image","/app/image"})
public class ImageController {

	private Logger  logger = LoggerFactory.getLogger(ImageController.class);
	
	@Value("${platform.DOMAIN}")
	private String domain;
	
	@Value("${file.staticPath}")
    private String staticPath;
 	
    @Value("${file.uploadFolder}")
    private String uploadFolder;
    
    @Value("${file.imageFolder}")
    private String imageFolder;
	
	//上传照片
	@RequestMapping(value = "/addPhoto",method = RequestMethod.POST)
	public  ResponseData addPhoto(@RequestParam("file") MultipartFile file,HttpServletRequest request) {	
        try {
            if(null!=file){
                //生成uuid作为文件名称
                String uuid=UUID.randomUUID().toString().replaceAll("-","");
                logger.debug("文件名称："+uuid);
                //获得文件类型（判断如果不是图片文件类型，则禁止上传）
                String contentType=file.getContentType();
                logger.debug("文件类型："+contentType);
            	//获得文件后缀名称
                String imageName=contentType.substring(contentType.indexOf("/")+1);
                logger.debug("文件后缀名称："+imageName);
	           if(ImageUtils.isImage(imageName)) {
	                String filePath="/images/";
	                //根据日期来创建对应的文件夹
	              //  String datePath=new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
	               // logger.debug("日期："+datePath);
	                //日期
	                //String path=filePath+datePath;
	                String path=filePath;
	                //如果不存在，则创建新文件夹
	                File f=new File(imageFolder+path);
	                if(!f.exists()){
	                    f.mkdirs();
	                }
	                //新生成的文件名称
	                String fileName=uuid+"."+imageName;
	               // logger.debug("新生成的文件名称："+fileName);
	                //图片保存的完整路径
	                String pathName=path+fileName;
	                logger.debug(pathName);
	                //复制操作
	                //将图片从源位置复制到目标位置
	                file.transferTo(new File(imageFolder+pathName));
	                Map<String, Object> map = new HashMap<String, Object>();
	                map.put("src", staticPath+pathName);
	                return ResponseData.success(map);
	                }
	           return ResponseData.error("上传的类型有误");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return ResponseData.error("上传失败");
	}
	
	
	  @RequestMapping(value = "/upload", method={RequestMethod.POST,RequestMethod.GET})
	    @ResponseBody
	    public ResponseData upload(@RequestParam(value = "file", required = false) MultipartFile multipartFile,
	            HttpServletRequest request) {
		   Map<String,Object> map = new HashMap<String, Object>();
	        FileEntity entity = new FileEntity();
	        FileUploadTool fileUploadTool = new FileUploadTool();
	        try {
	            entity = fileUploadTool.createFile(null,multipartFile, request);
	            if (entity != null) {
	                map.put("src", entity.getPath());
	                return ResponseData.success(200,"上传成功",map);
	            } else {
	                return ResponseData.error("上传失败");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return ResponseData.error("上传失败");
	    }
	
	@RequestMapping(value = "/uploadImg",method = RequestMethod.POST)
	public  ResponseData uploadImg(@RequestParam("file") MultipartFile file,HttpServletRequest request) {	
        try {
            if(null!=file){
                //获得当前项目所在路径
               // String pathRoot=request.getSession().getServletContext().getRealPath("");
                //生成uuid作为文件名称
                String uuid=UUID.randomUUID().toString().replaceAll("-","");
                //获得文件类型（判断如果不是图片文件类型，则禁止上传）
                String contentType=file.getContentType();
            	//获得文件后缀名称
                String imageName=contentType.substring(contentType.indexOf("/")+1);
	           if(ImageUtils.isImage(imageName)) {
	                String filePath="/images/";
	                //根据日期来创建对应的文件夹
	                //String datePath=new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
	              //  logger.debug("日期："+datePath);
	                //日期
	              //  String path=filePath+datePath;
	                String path=filePath;
	                //如果不存在，则创建新文件夹
	                File f=new File(imageFolder+path);
	                if(!f.exists()){
	                    f.mkdirs();
	                }
	                //新生成的文件名称
	                String fileName=uuid+"."+imageName;
	                //图片保存的完整路径
	               String pathName=path+fileName;
	                logger.debug(pathName);
	                //复制操作
	                //将图片从源位置复制到目标位置
	                file.transferTo(new File(uploadFolder+pathName));
	                Map<String, Object> map = new HashMap<String, Object>();
	                map.put("src",domain +staticPath+pathName);
	                return ResponseData.success(0,"上传成功",map);
	                }
	           return ResponseData.error("上传的类型有误");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return ResponseData.error("上传失败");
	}
	
	
	
}
