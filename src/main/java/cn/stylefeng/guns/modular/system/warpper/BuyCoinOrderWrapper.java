package cn.stylefeng.guns.modular.system.warpper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.UserBonusSetting;
import cn.stylefeng.guns.modular.system.entity.UserPayMethodFeeSetting;
import cn.stylefeng.guns.modular.system.entity.UserRecommendRelation;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class BuyCoinOrderWrapper extends BaseControllerWrapper {

	public BuyCoinOrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public BuyCoinOrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public BuyCoinOrderWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public BuyCoinOrderWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Long sellerId = (Long) map.get("sellerId");
    	Seller seller = ConstantFactory.me().getSeller(sellerId);
		map.put("phone", seller.getAccount());
		map.put("nickName", seller.getNickName());
		map.put("uId", seller.getCode());
    	Long userId = (Long) map.get("buyerId");
    	String buyerAccount = ConstantFactory.me().getUserAccountCodeById(userId);
    	map.put("buyerAccount", buyerAccount);
    	Integer type = (Integer) map.get("payMethodType");
    	Double number = (Double) map.get("number");
    	number = number == null ? 0:number;
    	Double feePrice = (Double) map.get("feePrice");
    	if(feePrice != null && feePrice >0) {
    		map.put("feeRatio", feePrice+"%");
    		map.put("merchantFee",new BigDecimal(number*Double.valueOf(feePrice)/100).setScale(2, BigDecimal.ROUND_HALF_UP));
    	}else {
    		UserPayMethodFeeSetting feeSetting = ConstantFactory.me().getUserPayMethodFeeSetting(userId);
        	if(feeSetting != null) {
        		if(type.equals(1)
						|| type.equals(5)
						|| type.equals(7) ) {
        			map.put("feeRatio", feeSetting.getAlipayRatio()+"%");
        			map.put("merchantFee", new BigDecimal(number*feeSetting.getAlipayRatio()/100).setScale(2, BigDecimal.ROUND_HALF_UP));
        		}else if(type.equals(2)
						|| type.equals(6)
						|| type.equals(8)
						|| type.equals(9)) {
        			map.put("feeRatio", feeSetting.getWxRatio()+"%");
        			map.put("merchantFee", new BigDecimal(number*feeSetting.getWxRatio()/100).setScale(2, BigDecimal.ROUND_HALF_UP));
        		}else if(type.equals(3)){
        			map.put("feeRatio", feeSetting.getCardRatio()+"%");
        			map.put("merchantFee", new BigDecimal(number*feeSetting.getCardRatio()/100).setScale(2, BigDecimal.ROUND_HALF_UP));
        		}else if(type.equals(4)){
					map.put("feeRatio", feeSetting.getCloudPayRatio()+"%");
					map.put("merchantFee", new BigDecimal(number*feeSetting.getCloudPayRatio()/100).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
        	}else {
        		map.put("merchantFee", "0");
        	}
    	}
    	
    	Double agentFeePrice = (Double) map.get("agentFeePrice");
    	if(agentFeePrice != null && agentFeePrice>0) {
    		map.put("agentFee", new BigDecimal(number*Double.valueOf(agentFeePrice)/100).setScale(4, BigDecimal.ROUND_HALF_UP));
    		map.put("agentFeeRatio", agentFeePrice+"%");
    	}else {
    		UserRecommendRelation relation = ConstantFactory.me().getUserRecommendRelationByUserId(userId);
        	UserBonusSetting setting = new UserBonusSetting();
        	setting.setAgentId(relation.getRecommendId());
        	setting.setUserId(userId);
        	setting = ConstantFactory.me().getUserBonusSetting(setting);
        	if(setting != null) {
        		if(type.equals(1)
						|| type.equals(5)
						|| type.equals(7)) {
        			map.put("agentFee",new BigDecimal(number*setting.getAlipayRatio()/100).setScale(4, BigDecimal.ROUND_HALF_UP));
        			map.put("agentFeeRatio", setting.getAlipayRatio()+"%");
        		}else if(type.equals(2)
						|| type.equals(6)
						|| type.equals(8)
						|| type.equals(9)) {
        			map.put("agentFee", new BigDecimal(number*setting.getWxRatio()/100).setScale(4, BigDecimal.ROUND_HALF_UP));
        			map.put("agentFeeRatio", setting.getWxRatio()+"%");
        		}else if (type.equals(3)){
        			map.put("agentFee", new BigDecimal(number*setting.getCardRatio()/100).setScale(4, BigDecimal.ROUND_HALF_UP));
        			map.put("agentFeeRatio", setting.getCardRatio()+"%");
        		}else if (type.equals(4)){
					map.put("agentFee", new BigDecimal(number*setting.getCloudPayRatio()/100).setScale(4, BigDecimal.ROUND_HALF_UP));
					map.put("agentFeeRatio", setting.getCloudPayRatio()+"%");
				}
        	}else {
        		map.put("agentFee", "0");
        	}
    	}
    	Integer status = (Integer) map.get("status");
    	//状态：1:表示未支付，2：表示支付成功，等待确认到账，3：卖家确认，待买家确认，4：已完成，5：已超时，6申诉中，7：取消
    	if(status ==1) {
    		map.put("statusName", "待买家支付");
    	}else if(status ==2) {
    		map.put("statusName", "待卖家确认收款");
    	}else if(status ==3) {
    		map.put("statusName", "待买家确认收款");
    	}else if(status ==4) {
    		map.put("statusName", "订单已完成");
    	}else if(status ==5) {
    		map.put("statusName", "订单已超时");
    	}else if(status ==6) {
    		map.put("statusName", "申诉中");
    	}else if(status ==7) {
    		map.put("statusName", "订单已取消");
    	}else {
    		map.put("statusName", null);
    	}
    	
    	if(type ==1) {
			map.put("typeName", "支付宝");
		}else if(type ==2) {
			map.put("typeName", "微信");
		}else if(type.equals(3)){
			map.put("typeName", "银行卡");
		}else if(type.equals(4)){
			map.put("typeName", "网银转账");
		}else if(type.equals(5)){
			map.put("typeName", "支付宝手机号码转账");
		}else if(type.equals(6)){
			map.put("typeName", "微信手机号码转账");
		}else if(type.equals(7)){
			map.put("typeName", "支付宝转银行卡");
		}else if(type.equals(8)){
			map.put("typeName", "微信转银行卡");
		}else if(type.equals(9)){
			map.put("typeName", "微信赞赏码");
		}
    	
    	Integer isAppeal = (Integer) map.get("isAppeal");
    	if(isAppeal != null && isAppeal ==1) {
    		map.put("appealName", "是");
    	}else {
    		map.put("appealName", "否");
    	}
    	
    	Integer isSuccess = (Integer) map.get("isSuccess");
    	if(isSuccess != null && isSuccess ==1) {
    		map.put("isSuccessName", "回调成功");
    	}else {
    		map.put("isSuccessName", "");
    	}
		Integer orderCode = (Integer) map.get("orderCode");
		if(orderCode != null && orderCode ==1) {
			map.put("orderCodeName", "正常订单");
		}else if(orderCode != null && orderCode ==2) {
			map.put("orderCodeName", "补空单");
		}else if(orderCode != null && orderCode ==3) {
			map.put("orderCodeName", "返补单");
		}else if(orderCode != null && orderCode ==4) {
			map.put("orderCodeName", "重新激活订单");
		}
    }
}
