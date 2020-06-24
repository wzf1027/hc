package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class OtcpOrderWrapper extends BaseControllerWrapper {

	public OtcpOrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public OtcpOrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public OtcpOrderWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public OtcpOrderWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {    	
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
    		map.put("statusName", "买家取消交易");
    	}else {
    		map.put("statusName", null);
    	}
    	
    	Integer isAppeal = (Integer) map.get("isAppeal");
    	if(isAppeal ==null || isAppeal ==0) {
    		map.put("appealName", "暂无申诉");
    	}else if(isAppeal ==1) {
    		map.put("appealName", "申诉中");
    	}else if(isAppeal ==2) {
    		map.put("appealName", "买家胜");
    	}else if(isAppeal ==3) {
    		map.put("appealName", "卖家胜");
    	}else {
    		map.put("appealName", "取消申诉");
    	}
    	
    	
    	Integer payMethodType = (Integer) map.get("payMethodType");
    	if(payMethodType != null && payMethodType == 1) {
    		map.put("payMethodTypeName", "支付宝");
    	}else if(payMethodType != null && payMethodType == 2) {
    		map.put("payMethodTypeName", "微信");
    	}else if(payMethodType != null && payMethodType == 3) {
    		map.put("payMethodTypeName", "银行卡");
    	}else {
    		map.put("payMethodTypeName", "");
    	}
    	
//    	String sellerPhone = (String) map.get("sellerPhone");
//    	if(StringUtils.isBlank(sellerPhone)) {
//    		map.put("sellerPhone", ConstantFactory.me().getUserAccountById(Long.valueOf(map.get("sellerId").toString())));
//    	}
		map.put("buyerPhone", ConstantFactory.me().getSeller(Long.valueOf(map.get("buyerId").toString())).getAccount());
	//	map.put("sellerPhone", ConstantFactory.me().getUserAccountCodeById(Long.valueOf(map.get("sellerId").toString())));
    	Integer appealerRole = (Integer) map.get("appealerRole");
    	Long appealerId = (Long) map.get("appealerId");
    	if(appealerRole != null && appealerId != null) {
    		if(appealerRole ==1 || appealerRole ==3) {//会员或者承兑商
    			String appealer = ConstantFactory.me().getSeller(appealerId).getAccount();
    			map.put("appealer", appealer);
    		}else {//商户
    			String appealer = ConstantFactory.me().getUserAccountCodeById(appealerId);
    			map.put("appealer", appealer);
    		}
    	}
    }
}
