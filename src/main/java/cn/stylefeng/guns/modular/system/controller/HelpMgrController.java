package cn.stylefeng.guns.modular.system.controller;

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
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.Help;
import cn.stylefeng.guns.modular.system.service.HelpMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;


@Controller
@RequestMapping("/helpMgr")
public class HelpMgrController extends BaseController {
	

	 private String PREFIX = "/modular/system/help/";

	    @Autowired
	    private HelpMgrService helpMgrService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "help.html";
	    }
	

	    @RequestMapping("/help_add")
	    public String helpAdd() {
	        return PREFIX + "help_add.html";
	    }


	    @RequestMapping("/help_update/{helpId}")
	    public String helpUpdate(@PathVariable Long helpId, Model model) {
	        Help help = this.helpMgrService.getById(helpId);
	        model.addAllAttributes(BeanUtil.beanToMap(help));
	        LogObjectHolder.me().set(help);
	        return PREFIX + "help_edit.html";
	    }



	    @RequestMapping(value = "/list")
	    @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.helpMgrService.list(condition);
//	        Page<Map<String, Object>> wrap = new CarouselWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(list);
	    }


	    @RequestMapping(value = "/add")
	    @ResponseBody
	    public Object add(Help help) {
	    	this.helpMgrService.save(help);
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/delete")
	    @ResponseBody
	    public Object delete(@RequestParam Long helpId) {
	        this.helpMgrService.removeById(helpId);
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(Help help) {
	    	Help old = this.helpMgrService.getById(help.getId());
	    	old.setContent(help.getContent());
	    	old.setTitle(help.getTitle());
	        this.helpMgrService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
}
