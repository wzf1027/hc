package cn.stylefeng.guns.core.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {

	private static Logger logger =LoggerFactory.getLogger(ImageUtils.class);
	
	
	/**
	 * 判断是否图片
	 * @param fileSuffix
	 * @return
	 */
	public static final boolean isImage(String fileSuffix) {
		   if(!StringUtils.equalsIgnoreCase(fileSuffix, "jpg")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "jpeg")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "bmp")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "gif")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "png")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "txt")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "doc")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "docx")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "xls")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "xlsx")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "csv")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "ppt")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "pptx")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "pdf")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "wps")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "et")
			        && !StringUtils.equalsIgnoreCase(fileSuffix, "dps"))
			            return false;
			        return true;
	}
	
	public static String addPhoto(MultipartFile file,String pathRoot) {
		 String str="";
	        try {
	            if(null!=file){
	                //获得当前项目所在路径
	                System.out.println("当前项目所在路径："+pathRoot);
	                //生成uuid作为文件名称
	                String uuid=UUID.randomUUID().toString().replaceAll("-","");
	                System.out.println("文件名称："+uuid);
	                //获得文件类型（判断如果不是图片文件类型，则禁止上传）
	                String contentType=file.getContentType();
	                System.out.println("文件类型："+contentType);
	                //获得文件后缀名称
	                String imageName=contentType.substring(contentType.indexOf("/")+1);
	                System.out.println("文件后缀名称："+imageName);

	                String filePath="static/upload/images/";
	                //根据日期来创建对应的文件夹
	                String datePath=new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
	                System.out.println("日期："+datePath);
	                //根据id分类来创建对应的文件夹
	              //  String leagueIdPath=league_id+"/";
	                //日期
	                String path=filePath+datePath;
	                //联赛id
	                //String path=filePath+leagueIdPath;
	                //如果不存在，则创建新文件夹
	                File f=new File(pathRoot+path);
	                if(!f.exists()){
	                    f.mkdirs();
	                }
	                //新生成的文件名称
	                String fileName=uuid+"."+imageName;
	                System.out.println("新生成的文件名称："+fileName);
	                //图片保存的完整路径
	                String pathName=path+fileName;
	                System.out.println(pathName);

	                //获取所属联赛ID
	               // int leagueID=Integer.parseInt(league_id);
	                //图片的静态资源路径
	                String staticPath="/upload/images/"+datePath+"/"+fileName;
	                System.out.println("静态资源路径："+staticPath);

	                //复制操作
	                //将图片从源位置复制到目标位置
	                file.transferTo(new File(pathRoot+pathName));
	                str = datePath+fileName;
	            }
	            else{
	                System.out.println("文件为空");
	            }
	        }catch (IOException e){
	            e.printStackTrace();
	            return str;
	        }
	        return str;
	}
	
	
	/**
	 * 上传图片
	 * @param file
	 * @param pathRoot
	 * @return
	 */
	public static Map<String,Object> updatePhoto(MultipartFile file,String pathRoot){
		if(file == null)return null;
		Map<String,Object> map = null;
		String dateFolder=  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String path="/carSkypegmvcnImage/"+dateFolder;  
		 File fileDir = new File(pathRoot+path);
		 if(!fileDir.exists()) {
			 fileDir.mkdirs();
		 }
		if(file != null) {
			Object object = new Object();
			try {
					if(null != file.getOriginalFilename() && !"".equals(file.getOriginalFilename().trim())
							&& !"null".equals(file.getOriginalFilename())) {
						map = new HashMap<String, Object>();
						synchronized (object) {
							 //生成uuid作为文件名称  
				            String uuid = UUID.randomUUID().toString().replaceAll("-","");  
				            //获得文件类型（可以判断如果不是图片，禁止上传）  
				            String contentType=file.getContentType();  
				            //获得文件后缀名称  
				            String fileSuffix=contentType.substring(contentType.indexOf("/")+1); 
				            if(!isImage(fileSuffix)) {
				            	throw new RuntimeException("上传图片格式有误，请上传以jpge,jpg,png,bmp的格式");
				            }
				            String pathname = path+"/" +uuid+"."+fileSuffix;
				            logger.info("上传路径:"+pathRoot+pathname);
				            File multipartFile = new File(pathRoot+pathname);
				            file.transferTo(multipartFile);
				            map.put("key", uuid);
				            map.put("src", pathname);
						}	
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 *批量上传图片
	 * @param files 
	 * @param pathRoot
	 * @return
	 */
	public static  List<Map<String,Object>> batchUploadPhoto(MultipartFile[] files,String pathRoot){
		 List<Map<String,Object>> listImagePath=null;
		if(files != null && files.length > 0) {
			String dateFolder=  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String path="/carProjectImage/"+dateFolder;  
			 File fileDir = new File(pathRoot+path);
			 if(!fileDir.exists()) {
				 fileDir.mkdirs();
			 }
			Object object = new Object();
			listImagePath=new ArrayList<Map<String,Object>>();  
			try {
				for (MultipartFile multipartFile : files) {
					if(null != multipartFile  && null != multipartFile.getOriginalFilename()
							&& !"".equals(multipartFile.getOriginalFilename().trim())
							&& !"null".equals(multipartFile.getOriginalFilename())) {
						
						Map<String,Object> map = new HashMap<String, Object>();
						
						synchronized (object) {
							 //生成uuid作为文件名称  
				            String uuid = UUID.randomUUID().toString().replaceAll("-","");  
				            //获得文件类型（可以判断如果不是图片，禁止上传）  
				            String contentType=multipartFile.getContentType();  
				            //获得文件后缀名称  
				            String fileSuffix=contentType.substring(contentType.indexOf("/")+1); 
				            if(!isImage(fileSuffix)) {
				            	throw new Exception("上传图片格式有误，请上传以jpge,jpg,png,bmp的格式");
				            }
				            String pathname = path+"/" +uuid+"."+fileSuffix;
				            logger.info("上传路径:"+pathRoot+pathname);
				            File file = new File(pathRoot+pathname);
				            multipartFile.transferTo(file);
				            map.put("key", uuid);
				            map.put("src", pathname);
				            listImagePath.add(map);
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listImagePath;
	}

	
}
