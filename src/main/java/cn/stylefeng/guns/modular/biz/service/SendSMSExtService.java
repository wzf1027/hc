package cn.stylefeng.guns.modular.biz.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.stylefeng.guns.core.util.HttpUtil;

@Service
public class SendSMSExtService{
    
	
	@Value("${sms.switch}")
	private String isSwitch;
	
	@Value("${sms.password}")
	private String smsPassword;
	
	@Value("${sms.userName}")
	private String smsUserName;
	
    public boolean sendSms(String content, String phone,Long typeId) {
    	if("1".equals(isSwitch)) {
    		return true;
    	}
		boolean i=false;
		String r=organizationData(phone, content,typeId);
		String smsUrl = "https://dx.ipyy.net/sms.aspx?action=send&userid=&"+r;
	    try {
	        URL url = new URL(smsUrl);
	        URLConnection con = url.openConnection();
	        con.setDoOutput(true);
	        con.setRequestProperty("Cache-Control", "no-cache");
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
	        String result=HttpUtil.sendPost(smsUrl,null);
	        Document doc =null;
			try {
				doc = DocumentHelper.parseText(result);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			// 获取根节点
			Element rootElt = doc.getRootElement();
			// 获取根节点下的子节点的值
			String returnstatus = rootElt.elementText("returnstatus").trim();
	        if(returnstatus.equals("Success")){
	        	i=true;
	        }else{
	        	i=false;
	        }
	   
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return i;
    }


    private  String organizationData(String phone, String content,Long typeId) {
        StringBuilder sendBuilder = new StringBuilder();
    	 sendBuilder.append("account="+smsUserName);//机构ID:用户登录名MYC001
         sendBuilder.append("&password="+smsPassword);//密码
        sendBuilder.append("&mobile=").append(phone);//接收手机号，多个号码间以逗号分隔且最大不超过100个号码
        try {
        	sendBuilder.append("&content=").append(URLEncoder.encode(content, "UTF-8"));
            //发送内容,标准内容不能超过70个汉字
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendBuilder.append("&sendTime=");//发送时间
        sendBuilder.append("&extno=");
        sendBuilder.append("&code=8");
        return sendBuilder.toString();
    }

    
    
 // 字符编码成HEX
 		public static String encodeHexStr(int dataCoding, String realStr) {
 			String strhex = "";
 			try {
 				byte[] bytSource = null;
 				if (dataCoding == 15) {
 					bytSource = realStr.getBytes("GBK");
 				} else if (dataCoding == 3) {
 					bytSource = realStr.getBytes("ISO-8859-1");
 				} else if (dataCoding == 8) {
 					bytSource = realStr.getBytes("UTF-16BE");
 				} else {
 					bytSource = realStr.getBytes("ASCII");
 				}
 				strhex = bytesToHexString(bytSource);

 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 			return strhex;
 		}
    
 		/** */
		/**
		 * 把字节数组转换成16进制字符串
		 * 
		 * @param bArray
		 * @return
		 */
		public static final String bytesToHexString(byte[] bArray) {
			StringBuffer sb = new StringBuffer(bArray.length);
			String sTemp;
			for (int i = 0; i < bArray.length; i++) {
				sTemp = Integer.toHexString(0xFF & bArray[i]);
				if (sTemp.length() < 2)
					sb.append("0");
				sb.append(sTemp.toUpperCase());
			}
			return sb.toString();
		}
    
    
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
 
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
 
    public static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
 
    public static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }
    
    public static void main(String[] args) {
    	 String testUsername = "wzf1027"; //在短信宝注册的用户名
         String testPassword = "wyh10271203.."; //在短信宝注册的密码
         String testPhone = "18802075356";
         String testContent = "【万千购】您的验证码是1234,５分钟内有效。若非本人操作请忽略此消息。"; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如 测一下、您好。否则可能会收不到
  
         String httpUrl = "http://api.smsbao.com/sms";
  
         StringBuffer httpArg = new StringBuffer();
         httpArg.append("u=").append(testUsername).append("&");
         httpArg.append("p=").append(md5(testPassword)).append("&");
         httpArg.append("m=").append(testPhone).append("&");
         httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
  
         String result = request(httpUrl, httpArg.toString());
         
         System.out.println(result);
	}
}
