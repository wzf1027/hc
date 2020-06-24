package cn.stylefeng.guns.core.util;

public interface Constant {

		//redis保存用户token有效时间
		public static final long SAVEUSERTIME = 3*24*60*60;
		//在线时长（秒）
		public static final long ONLINETIME = 5;
		
		public static final String TOKEN = "KJ_TOKEN_";
		
		public static final String ONE_LEVEL = "ONE_LEVEL";
		
		public static final String TWO_LEVEL = "TWO_LEVEL";
		
		public static final String THIRD_LEVEL = "THIRD_LEVEL";
		
		public static final String FOUR_LEVEL = "FOUR_LEVEL";

		public static final String COIN_PRICE_RATE = "COIN_PRICE_RATE";

		public static final String COIN_SERVICE_PRICE = "COIN_SERVICE_PRICE";

		public static final String COIN_CONSUMPTION_ENERGY = "COIN_CONSUMPTION_ENERGY";

		public static final String COIN_RATE_ENERGY = "COIN_RATE_ENERGY";

		public static final String SMS = "SMS_";
		
		public static final String RECHARGER_TOKEN="RECHARGER_TOKEN";
		
		public static final String BUY_MEMBER_TOKEN="RECHARGER_TOKEN";
		
		public static final long PAY_TOKEN_TIME = 3*60;

		public static final String DRAW_PAY_TOKEN = "DRAW_PAY_TOKEN";
		
		
	    /**
	     * 今日发行量
	     */
	    String COIN_BUY_NUMBER = "COIN_BUY_NUMBER";
	    
	    /**
	     * 价格
	     */
	    String COIN_PRICE = "COIN_PRICE";

   		 String CLIENT_TOKEN ="CLIENT_TOKEN_" ;
}
