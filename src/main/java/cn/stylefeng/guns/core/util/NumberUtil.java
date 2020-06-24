package cn.stylefeng.guns.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class NumberUtil {

	public static boolean isNum(String str){ 
		if (StringUtils.isBlank(str))
            return false;
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$"); 
	} 
	
	 public static boolean isNumLegal(String str) {
		 if (StringUtils.isBlank(str))
	            return false;
	        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
	        Pattern p = Pattern.compile(regExp);
	        Matcher m = p.matcher(str);
	        return m.matches();
	    }
	
	 /**
	  * 判断字符串是否为正整数
	  * @param string
	  * @return
	  */
	 public static boolean isPureDigital(String str) {
	        if (StringUtils.isBlank(str))
	            return false;
	        String regEx1 = "\\d+";
	        Pattern p;
	        Matcher m;
	        p = Pattern.compile(regEx1);
	        m = p.matcher(str);
	        if (m.matches())
	            return true;
	        else
	            return false;
	    }
	 
	 /**
	  * 判断浮点数（double和float）
	  * @param str
	  * @return
	  */
	 public static boolean isDouble(String str) {
	 	if (StringUtils.isBlank(str)) {
	 		return false;
	 	}
	 	Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
	 	return pattern.matcher(str).matches();
	 }
	 /**
	  *	 验证身份证是否正确
	  * @param cardid
	  * @return
	  */
	 public static boolean getValidIdCard(String cardid){
		 
	        String ls_id = cardid;
	        if(ls_id.length() != 18)
	        {
	            return false;
	        }
	        char[] l_id = ls_id.toCharArray();
	        int l_jyw = 0;
	        int[] wi = new int[]{7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1};
	        char[] ai= new char[]{'1','0','X','9','8','7','6','5','4','3','2'};
	        for(int i =0 ; i < 17; i++)
	        {
	            if(l_id[i] < '0' || l_id[i] > '9')
	            {
	                return false;
	            }
	            l_jyw += (l_id[i] -'0')*wi[i];
	        }
	        l_jyw = l_jyw % 11;
	        System.out.println("ai[l_jyw]:"+ai[l_jyw]+"---l_id[17]:"+l_id[17]);
	        if(ai[l_jyw] != l_id[17])
	        {
	            return false;
	        }
	        return true;
	    }
		/**
		 * 校验银行卡卡号
		 * 
		 * @param cardId
		 * @return
		 */
		public static boolean checkBankCard(String cardId) {
			char bit = getBankCardCheckCode(cardId
					.substring(0, cardId.length() - 1));
			if (bit == 'N') {
				return false;
			}
			return cardId.charAt(cardId.length() - 1) == bit;
		}
	 
		/**
		 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
		 * 
		 * @param nonCheckCodeCardId
		 * @return
		 */
		public static char getBankCardCheckCode(String nonCheckCodeCardId) {
			if (nonCheckCodeCardId == null
					|| nonCheckCodeCardId.trim().length() == 0
					|| !nonCheckCodeCardId.matches("\\d+")) {
				// 如果传的不是数据返回N
				return 'N';
			}
			char[] chs = nonCheckCodeCardId.trim().toCharArray();
			int luhmSum = 0;
			for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
				int k = chs[i] - '0';
				if (j % 2 == 0) {
					k *= 2;
					k = k / 10 + k % 10;
				}
				luhmSum += k;
			}
			return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
		}

		 public static boolean isPositiveNumber(Object value){
		    if (value != null) {
		      try{
		        double result = Double.parseDouble(value.toString());
		        return result > 0.0D;
		      }catch (Exception e){
		        return false;
		      }
		    }
		    return false;
		  }


		  public static final boolean isPositiveInteger(Object value){
		    return (value != null) && (NumberUtils.isDigits(value.toString())) && (Long.parseLong(value.toString()) > 0L);
		  }
		  
		  /**
			 * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。
			 * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
			 * <pre>
			 * 	"3.1415926", 1			--> 3.1
			 * 	"3.1415926", 3			--> 3.142
			 * 	"3.1415926", 4			--> 3.1416
			 * 	"3.1415926", 6			--> 3.141593
			 * 	"1234567891234567.1415926", 3	--> 1234567891234567.142
			 * </pre>
			 * @param String类型的数字对象
			 * @param precision  小数精确度总位数,如2表示两位小数
			 * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
			 */
			public static String keepPrecision(String number, int precision) {
				BigDecimal bg = new BigDecimal(number);
				return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
			}
		 
			/**
			 * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。<br>
			 * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
			 * 如果给定的数字没有小数，则转换之后将以0填充；例如：int 123  1 --> 123.0<br>
			 * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
			 * @param String类型的数字对象
			 * @param precision  小数精确度总位数,如2表示两位小数
			 * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
			 */
			public static String keepPrecision(Number number, int precision) {
				return keepPrecision(String.valueOf(number), precision);
			}
		 
			/**
			 * 对double类型的数值保留指定位数的小数。<br>
			 * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
			 * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
			 * @param number  要保留小数的数字
			 * @param precision 小数位数
			 * @return double 如果数值较大，则使用科学计数法表示
			 */
			public static double keepPrecision(double number, int precision) {
				BigDecimal bg = new BigDecimal(number);
				return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		 
			/**
			 * 对float类型的数值保留指定位数的小数。<br>
			 * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
			 * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
			 * @param number  要保留小数的数字
			 * @param precision 小数位数
			 * @return float 如果数值较大，则使用科学计数法表示
			 */
			public static float keepPrecision(float number, int precision) {
				BigDecimal bg = new BigDecimal(number);
				return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
			}
		 	
			/**
			 * 将double的科学计数法的千分位转换了
			 * @param number
			 * @param precision
			 * @return
			 */
			public static String changeDoulePrecision(double number, int precision) {
				 DecimalFormat df = new DecimalFormat();
				 df.setMaximumFractionDigits(precision);//这里是小数位   
				 String format = df.format(number);
				 String removeStr = ",";//去掉千分符号
				 String replace = format.replace(removeStr, "");
				 return replace;
			}
}
