package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.model.UserDto;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.UserWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/agentMgr")
public class AgentController extends BaseController {

	   private String PREFIX = "/modular/system/agent/";
	   
	   @Autowired
	   private UserService userService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "agent.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(
				@RequestParam(required = false) String phone,
	            @RequestParam(required = false) String timeLimit) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
//	        String roleId = "4";
	        Page<Map<String, Object>> list = this.userService.selectUsersByRoleId(phone,beginTime,endTime,4);
	        Page<Map<String, Object>> wrap = new UserWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/agent_add")
	    public String addView() {
	        return PREFIX + "agent_add.html";
	    }
	   
	   
	   @RequestMapping("/add")
//	   @Permission(Const.ADMIN_NAME)
	   @ResponseBody
	   public ResponseData add(@Valid UserDto user, BindingResult result) {
	        if (result.hasErrors()) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        user.setRoleId("4");
	        this.userService.addAgentUser(user);
	        return SUCCESS_TIP;
	    }
	   
	  
}
