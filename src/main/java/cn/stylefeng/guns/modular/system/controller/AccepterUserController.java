package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.UserWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/accepterUserMgr")
public class AccepterUserController extends BaseController {

    private static String PREFIX = "/modular/system/user/";
	
    @Autowired
    private UserService userService;
    
	 @RequestMapping("")
	 public String index() {
        return PREFIX + "accepter.html";
    }
	 
	@SuppressWarnings("rawtypes")
	@RequestMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String timeLimit) {
        //拼接查询条件
        String beginTime = "";
        String endTime = "";
        if (ToolUtil.isNotEmpty(timeLimit)) {
            String[] split = timeLimit.split(" - ");
            beginTime = split[0];
            endTime = split[1];
        }
	    Page<Map<String, Object>> users = userService.selectAccepterUser(name, beginTime, endTime);
	    Page wrapped = new UserWrapper(users).wrap();
	    return LayuiPageFactory.createPageInfo(wrapped);
    }

	 
	 
	 
	
}
