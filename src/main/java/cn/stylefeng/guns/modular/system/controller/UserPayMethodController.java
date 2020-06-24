package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.UserPayMethod;
import cn.stylefeng.guns.modular.system.model.UserPayMethodDto;
import cn.stylefeng.guns.modular.system.service.UserPayMethodService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/userPayMethodMgr")
public class UserPayMethodController extends BaseController {

	   private String PREFIX = "/modular/system/userPayMethod/";
	   
	   @Autowired
	   private UserPayMethodService userPayMethodService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "userPayMethod.html";
	    }
	   

	    @RequestMapping("/userPayMethod_add")
	    public String userPayMethodAdd() {
	        return PREFIX + "userPayMethod_add.html";
	    }

	    @RequestMapping("/userPayMethod_update/{payMethodId}")
	    public String userPayMethodUpdate(@PathVariable Long payMethodId, Model model) {
	        UserPayMethod payMethod = this.userPayMethodService.getById(payMethodId);
	        UserPayMethodDto payMethodDto = new UserPayMethodDto();
	        BeanUtil.copyProperties(payMethod, payMethodDto);
	        payMethodDto.setAccountName(payMethod.getName());
	        model.addAllAttributes(BeanUtil.beanToMap(payMethodDto));
	        LogObjectHolder.me().set(payMethodDto);
	        return PREFIX + "userPayMethod_edit.html";
	    }


	    @RequestMapping(value = "/list")
		   @ResponseBody
		    public Object list(String condition) {
	    	Long userId = ShiroKit.getUser().getId();
		        Page<Map<String, Object>> list = this.userPayMethodService.list(condition,userId);
		       // Page<Map<String, Object>> wrap = new SellerWithdrawCoinAppealOrderWrapper(list).wrap();
		        return LayuiPageFactory.createPageInfo(list);
		    }

	    @RequestMapping(value = "/add")
	    @ResponseBody
	    public Object add(UserPayMethod userPayMethod) {
	        userPayMethod.setUserId(ShiroKit.getUser().getId());
	        userPayMethod.setCreateTime(new Date());
	        userPayMethod.setType(3);
	        this.userPayMethodService.save(userPayMethod);
	        return SUCCESS_TIP;
	    }

	    /**
	     * 删除
	     */
	    @RequestMapping(value = "/delete")
	    @ResponseBody
	    public Object delete(@RequestParam Long payMethodId) {
	        this.userPayMethodService.removeById(payMethodId);
	        return SUCCESS_TIP;
	    }

	    /**
	     * 修改
	     */
	    @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(UserPayMethod userPayMethod) {
	        if (ToolUtil.isOneEmpty(userPayMethod, userPayMethod.getPayMethodId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        UserPayMethod old = this.userPayMethodService.getById(userPayMethod.getPayMethodId());
	        old.setName(userPayMethod.getName());
	        old.setCardBank(userPayMethod.getCardBank());
	        old.setCardBankName(userPayMethod.getCardBankName());
	        old.setType(3);
	        old.setAccount(userPayMethod.getAccount());
	        old.setUpdateTime(new Date());
	        this.userPayMethodService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
   
	
}
