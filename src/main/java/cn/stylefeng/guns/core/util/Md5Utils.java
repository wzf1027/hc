package cn.stylefeng.guns.core.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Md5Utils {
	/**
	 * 
	 * 
	 * @MD5加密小写 转大写需要使用toUpperCase()方法
	 *
	 */
	
	/**
	 * 
	 * 
	 * @MD5加密小写 转大写需要使用toUpperCase()方法
	 *
	 */
	
		private static final String hexDigIts[] = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
		public static String MD5Encode(String origin, String charsetname){
			String resultString = null;
			try{
				resultString = new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				if(null == charsetname || "".equals(charsetname)){
					resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
				}else{
					resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
				}
			}catch (Exception e){
			}
			return resultString;
		}


		public static String byteArrayToHexString(byte b[]){
			StringBuffer resultSb = new StringBuffer();
			for(int i = 0; i < b.length; i++){
				resultSb.append(byteToHexString(b[i]));
			}
			return resultSb.toString();
		}

		public static String byteToHexString(byte b){
			int n = b;
			if(n < 0){
				n += 256;
			}
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigIts[d1] + hexDigIts[d2];
		}
		/**
		 * 
		 *  测试MD5方法
		 */
		public static void main(String[] args) {
			//注释的是完整的加密串，不是必填的有值时需要排序加密，没有值时不参与加密
			//String string ="pay_amount=0.2&pay_applydate=2017-12-6 10:15:15&pay_attach=12&pay_bankcode=123&pay_callbackurl=http://www.baidu.com&pay_memberid=18516105pL&pay_notifyurl=http://www.baidu.com&pay_orderid=123456&pay_productid=1313&pay_productnamepay_productname=4234&key=112233";
			String string ="pay_amount=0.2&pay_applydate=2017-12-6 10:15:15&pay_bankcode=123&pay_callbackurl=http://www.baidu.com&pay_memberid=18516105pL&pay_notifyurl=http://www.baidu.com&pay_orderid=123456&key=112233";
			String md5Encode = Md5Utils.MD5Encode(string,"utf8");
			System.out.println(md5Encode.toUpperCase());
		}
		
		
		public static String GetMD5Code(String strObj) {
	        String resultString = null;
	        try {
	            resultString = new String(strObj);
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            resultString = byteToString(md.digest(strObj.getBytes()));
	        } catch (NoSuchAlgorithmException ex) {
	            ex.printStackTrace();
	        }
	        return resultString;
	    }
		
		 private static String byteToString(byte[] bByte) {
		        StringBuffer sBuffer = new StringBuffer();
		        for (int i = 0; i < bByte.length; i++) {
		            sBuffer.append(byteToArrayString(bByte[i]));
		        }
		        return sBuffer.toString();
		    }
		 
		    private static String byteToArrayString(byte bByte) {
		        int iRet = bByte;
		        if (iRet < 0) {
		            iRet += 256;
		        }
		        int iD1 = iRet / 16;
		        int iD2 = iRet % 16;
		        return hexDigIts[iD1] + hexDigIts[iD2];
		    }

		    /**
			 * 生成签名
			 * @param map
			 * @return
			 */
			public static String getSign(Map<String, String> map) {
				 String result = "";
				    try {
				        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
				        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
				        infoIds.sort(new Comparator<Map.Entry<String, String>>() {
				        public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				            return (o1.getKey()).compareTo(o2.getKey());
				        }
				        });
				        // 构造签名键值对的格式
				        StringBuilder sb = new StringBuilder();
				        for (Map.Entry<String, String> item : infoIds) {
				            if (item.getKey() != null || item.getKey() != "") {
				                String key = item.getKey();
				                String val = item.getValue();
				                if (!(val == "" || val == null)) {
				                    sb.append(key + "=" + val + "&");
				                }
				            }
				        }
				        result = sb.toString();
				    } catch (Exception e) {
				        return null;
				    }
				    return result;

			}  
}

