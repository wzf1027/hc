package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.TeamBonusSetting;
import cn.stylefeng.guns.modular.system.service.TeamBonusSettingService;
import cn.stylefeng.guns.modular.system.warpper.TeamBonusSettingWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/teamBonusSettingMgr")
public class TeamBonusSettingController extends BaseController {

	   private String PREFIX = "/modular/system/teamBonusSetting/";
	   
	   @Autowired
	   private TeamBonusSettingService teamBonusSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "teamBonusSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.teamBonusSettingService.list(condition);
	        Page<Map<String, Object>> wrap = new TeamBonusSettingWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   
	   @RequestMapping("/teamBonusSetting_add")
	    public String addView() {
	        return PREFIX + "teamBonusSetting_add.html";
	    }
	   
	   
	   @RequestMapping("/add")
//	   @Permission(Const.ADMIN_NAME)
	   @ResponseBody
	   public ResponseData add(TeamBonusSetting setting) {
		   if(setting.getMinPrice() == null || setting.getMinPrice() <=0) {
	        	return ResponseData.error("最小额度不能小于0");
	        }
	        if(setting.getBonusRatio() == null || setting.getBonusRatio() <0) {
	        	return ResponseData.error("返利比例不能小于0");
	        }
	        int count = this.teamBonusSettingService.count();
	        setting.setLevel(1);
	        if(count >=1) {
		        TeamBonusSetting old =  this.teamBonusSettingService.getLastTeamBonusSettingOne();
		        if(old != null) {
		        	if(setting.getMinPrice()<=old.getMinPrice()) {
		        		return ResponseData.error("最小额度不能小于等于上级");
		        	}
		        	if(old.getMaxPrice() == null || old.getMaxPrice()<=0) {
		        		return ResponseData.error("请先设置上级的最大额度");
		        	}
		        	if(setting.getMinPrice() <= old.getMaxPrice()) {
		        		return ResponseData.error("最小额度不能小于等于上级的最大额度");
		        	}
		        	if(setting.getMaxPrice() != null && setting.getMaxPrice() >0 
		        			&& setting.getMaxPrice() <=old.getMaxPrice()) {
		        		return ResponseData.error("最大额度不能小于等于上级"); 
		        	}
		        	setting.setLevel(old.getLevel()+1);
		        }
	        }
	        if(setting.getMaxPrice() != null && setting.getMaxPrice()==0) {
	        	setting.setMaxPrice(null);
	        }
	        this.teamBonusSettingService.save(setting);
	        return SUCCESS_TIP;
	    }
	   
	   @RequestMapping("/teamBonusSetting_update/{settingId}")
	    public String noticeUpdate(@PathVariable Long settingId, Model model) {
		   TeamBonusSetting setting = this.teamBonusSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "teamBonusSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(TeamBonusSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getMinPrice() == null || setting.getMinPrice() <=0) {
	        	return ResponseData.error("最小额度不能小于0");
	        }
	        if(setting.getBonusRatio() == null || setting.getBonusRatio() <=0) {
	        	return ResponseData.error("返利比例不能小于0");
	        }
	        if(setting.getMaxPrice() != null && setting.getMaxPrice() != 0 && setting.getMinPrice() > setting.getMaxPrice()) {
	        	return ResponseData.error("最小额度不能大于最大额度");
	        }
	        TeamBonusSetting old = this.teamBonusSettingService.getById(setting.getSettingId());
	        int count = this.teamBonusSettingService.count();
	        if(count >1) {
	        	TeamBonusSetting last =this.teamBonusSettingService.getLastTeamBonusSettingOne();
	        	if(!last.getLevel().equals(old.getLevel()) ||count >=1) {
	        		QueryWrapper<TeamBonusSetting> queryWrapper = new QueryWrapper<TeamBonusSetting>();
			        queryWrapper.eq("LEVEL",old.getLevel()-1);
			        TeamBonusSetting setting2 = this.teamBonusSettingService.getOne(queryWrapper);
			        if(setting2 != null) {
			        	if(setting.getMinPrice() <= setting2.getMinPrice()) {
			        		return ResponseData.error("最小额度不能小于等于上级的最小额度");
			        	}
			        	if(setting.getMinPrice() <= setting2.getMaxPrice()) {
			        		return ResponseData.error("最小额度不能小于等于上级的最大额度");
			        	}
			        }
	        	}
	        	
	        	QueryWrapper<TeamBonusSetting> queryWrapper = new QueryWrapper<TeamBonusSetting>();
		        queryWrapper.eq("LEVEL",old.getLevel()+1);
		        TeamBonusSetting setting2 = this.teamBonusSettingService.getOne(queryWrapper);
		        if(setting2 != null) {
		        	if(setting.getMinPrice()>setting2.getMinPrice()) {
		        		return ResponseData.error("最小额度不能大于下级最小额度");
		        	}
		        	if(setting.getMaxPrice()==null || setting.getMaxPrice() <=0|| setting.getMaxPrice()>setting2.getMinPrice()) {
		        		return ResponseData.error("最大额度不能大于下级最小额度");
		        	}
		        }
		        old.setMaxPrice(setting.getMaxPrice());
		        old.setMinPrice(setting.getMinPrice());
		        old.setBonusRatio(setting.getBonusRatio());
		        if(setting.getMaxPrice() == null || setting.getMaxPrice() <=0) {
		        	old.setMaxPrice(null);
		        }
	        }

	      this.teamBonusSettingService.updateSettingById(old);
	        return SUCCESS_TIP;
	    }
	   	
	    @RequestMapping(value = "/delete")
	    @ResponseBody
	    public Object delete(@RequestParam Long settingId) {
	    	TeamBonusSetting old = this.teamBonusSettingService.getById(settingId);
	    	TeamBonusSetting supOld = this.teamBonusSettingService.getLastTeamBonusSettingOne();
	    	if(supOld != null) {
	    		if(old.getLevel() <supOld.getLevel()) {
	    			return ResponseData.error("请先删除最高等级");
	    		}
	    	}
	        this.teamBonusSettingService.removeById(settingId);
	        return SUCCESS_TIP;
	    }

	   	
}
