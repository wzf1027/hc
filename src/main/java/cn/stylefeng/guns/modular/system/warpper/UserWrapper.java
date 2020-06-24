package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

/**
 * 用户管理的包装类
 *

 */
public class UserWrapper extends BaseControllerWrapper {

    public UserWrapper(Map<String, Object> single) {
        super(single);
    }

    public UserWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public UserWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public UserWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	String roleId = (String) map.get("roleId");
        map.put("sexName", ConstantFactory.me().getSexName((String) map.get("sex")));
        map.put("roleName", ConstantFactory.me().getRoleName(roleId));
        map.put("deptName", ConstantFactory.me().getDeptName((Long) map.get("deptId")));
       Integer isAuth =  (Integer) map.get("isAuth");
       isAuth = isAuth == null ?0:isAuth;
       if(isAuth ==0) {
    	   map.put("authName", "未提交审核");
       }else if(isAuth ==1) {
    	   map.put("authName", "审核中");
       }else if(isAuth ==2) {
    	   map.put("authName", "审核通过");
       }
       String name = (String) map.get("name");
       if(StringUtils.isBlank(name)) {
           map.put("name", map.get("phone")+"");
       }
       
       if("4".equals(roleId)) {
    	   List<UserWallter> wallterList =  ConstantFactory.me().getUserWallterListByUserId((Long)map.get("userId"));
    	   for (UserWallter userWallter : wallterList) {
			if(userWallter.getType().equals(1)) {
				map.put("usdtBalance",userWallter.getAvailableBalance());
		    	   map.put("usdtFrozen", userWallter.getFrozenBalance());
			}
			if(userWallter.getType().equals(2)) {
				map.put("otcpBalance",userWallter.getAvailableBalance());
		    	map.put("otcpFrozen", userWallter.getFrozenBalance());
			}
			map.put("merchantNumber", ConstantFactory.me().getMerchantNumberByAgentId((Long)map.get("userId")));
    	   } 
       }
       
       map.put("recommendPhone", ConstantFactory.me().getRecommendId((Long)map.get("userId")));
        map.put("statusName", ConstantFactory.me().getStatusName((String) map.get("status")));
    }

}
