package cn.stylefeng.guns.core.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadTool {

	private Logger logger = LoggerFactory.getLogger(FileUploadTool.class);
	
    private static FileUploadTool fileUploadTool;

    @Value("${file.staticPath}")
    private String staticPath;
    /**
     * 图片存放根目录下的子目录
     */
    @Value("${file.uploadFolder}")
    private String uploadFolder;


    /**
     * 文件最大500M
     */
    private static long upload_maxsize = 5000 * 1024 * 1024;
    /**
     * 文件允许格式
     */
    private static String[] allowFiles = {".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".xlsx", ".gif",
            ".png", ".jpg", ".jpeg", ".bmp", ".xls", ".mp4", ".flv", ".ppt", ".avi", ".mpg", ".wmv", ".3gp", ".mov",
            ".asf", ".asx", ".vob", ".wmv9", ".rm", ".rmvb",".qlv",".mkv"};
    /**
     * 允许转码的视频格式（ffmpeg）
     */
    private static String[] allowFLV = {".avi", ".mpg", ".wmv", ".3gp", ".mov", ".asf", ".asx", ".vob"};

    /**
     * 允许的视频转码格式(mencoder)
     */
    private static String[] allowAVI = {".wmv9", ".rm", ".rmvb"};


    public FileEntity createFile(String logoPathDir, MultipartFile multipartFile, HttpServletRequest request) {
    	FileEntity entity = new FileEntity();
        String pathName = "";
        boolean bflag = false;
        String fileName = multipartFile.getOriginalFilename().toString();
        logger.debug("文件全称：" + fileName + "~~~~~~~~~~~~~~");
        // 判断文件不为空
        if (multipartFile.getSize() != 0 && !multipartFile.isEmpty()) {
            bflag = true;
            // 判断文件大小
            if (multipartFile.getSize() <= upload_maxsize) {
                bflag = true;
                // 文件类型判断
                if (this.checkFileType(fileName)) {
                    bflag = true;
                } else {
                    bflag = false;
                    logger.debug("文件类型不允许");
                }
            } else {
                bflag = false;
                logger.debug("文件大小超范围");
            }
        } else {
            bflag = false;
            logger.debug("文件为空");
        }
        if (bflag) {
            //生成uuid作为文件名称
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            logger.debug("文件名称：" + uuid);
            //获得文件类型（判断如果不是图片文件类型，则禁止上传）
            String contentType = multipartFile.getContentType();
            logger.debug("文件类型：" + contentType);
            //获得文件后缀名称
            String imageName = contentType.substring(contentType.indexOf("/") + 1);
            logger.debug("文件后缀名称：" + imageName);
            String filePath = "/video/";
            //根据日期来创建对应的文件夹
            //String datePath = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
           // logger.debug("日期：" + datePath);
            //日期
         //   String path = filePath + datePath;
            String path = filePath ;
            //如果不存在，则创建新文件夹
            File f = new File(fileUploadTool.uploadFolder  + path);
            if (!f.exists()) {
                f.mkdirs();
            }
            //新生成的文件名称
            fileName = uuid + "." + imageName;
            System.out.println("新生成的文件名称：" + fileName);
            //图片保存的完整路径
            pathName = path + fileName;
            logger.debug(pathName);
            File filedirs = new File(fileUploadTool.uploadFolder  + pathName);
            // 转入文件
            try {
                multipartFile.transferTo(filedirs);
                // size存储为String
                String size = this.getSize(filedirs);
                entity.setSize(size);
                entity.setPath(fileUploadTool.staticPath+pathName);
                entity.setTitleAlter(fileName);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                entity.setUploadTime(timestamp);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return entity;


    }
    
    /**
     * 文件类型判断
     *
     * @param fileName
     * @return
     */
    private boolean checkFileType(String fileName) {
        Iterator<String> type = Arrays.asList(allowFiles).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }


        return false;
    }


    /**
     * 视频类型判断(flv)
     *
     * @param
     * @return
     */
    @SuppressWarnings("unused")
	private boolean checkMediaType(String fileEnd) {
        Iterator<String> type = Arrays.asList(allowFLV).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileEnd.equals(ext)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 视频类型判断(AVI)
     *
     * @param
     * @return
     */
    @SuppressWarnings("unused")
	private boolean checkAVIType(String fileEnd) {
        Iterator<String> type = Arrays.asList(allowAVI).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileEnd.equals(ext)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取文件扩展名
     *
     * @return string
     */
    @SuppressWarnings("unused")
	private String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 依据原始文件名生成新文件名
     * UUID：全局唯一标识符，由一个十六位的数字组成,由三部分组成：当前日期和时间、时钟序列、全局唯一的IEEE机器识别号
     *
     * @return string
     */
    @SuppressWarnings("unused")
	private String getName(String fileName) {
        Random random = new Random();
        return "" + random.nextInt(10000) + System.currentTimeMillis();
//return UUID.randomUUID().toString() + "_" + fileName;


    }


    /**
     * 文件大小，返回kb.mb
     *  
     *
     * @return
     */
    @SuppressWarnings("unused")
	private String getSize(File file) {
        String size = "";
        long fileLength = file.length();
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024) {
            size = df.format((double) fileLength) + "BT";
        } else if (fileLength < 1048576) {
            size = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            size = df.format((double) fileLength / 1048576) + "MB";
        } else {
            size = df.format((double) fileLength / 1073741824) + "GB";
        }


        return size;

    }

    @PostConstruct
    public void init() {
        fileUploadTool = this;
        fileUploadTool.staticPath = this.staticPath;
        fileUploadTool.uploadFolder = this.uploadFolder;
    }
}
