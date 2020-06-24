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
import cn.stylefeng.guns.modular.system.entity.Proclamation;
import cn.stylefeng.guns.modular.system.service.ProclamationMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;


@Controller
@RequestMapping("/proclamationMgr")
public class ProclamationMgrController extends BaseController {
	

	 private String PREFIX = "/modular/system/proclamation/";

	    @Autowired
	    private ProclamationMgrService proclamationMgrService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "proclamation.html";
	    }


	    @RequestMapping("/proclamation_add")
	    public String proclamationAdd() {
	        return PREFIX + "proclamation_add.html";
	    }


	    @RequestMapping("/proclamation_update/{proclamationId}")
	    public String proclamationUpdate(@PathVariable Long proclamationId, Model model) {
	        Proclamation proclamation = this.proclamationMgrService.getById(proclamationId);
	        model.addAllAttributes(BeanUtil.beanToMap(proclamation));
	        LogObjectHolder.me().set(proclamation);
	        return PREFIX + "proclamation_edit.html";
	    }



	    @RequestMapping(value = "/list")
	    @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.proclamationMgrService.list(condition);
	       // Page<Map<String, Object>> wrap = new CarouselWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(list);
	    }


	    @RequestMapping(value = "/add")
	    @ResponseBody
	    public Object add(Proclamation proclamation) {
	    	this.proclamationMgrService.save(proclamation);
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/delete")
	    @ResponseBody
	    public Object delete(@RequestParam Long proclamationId) {
	        this.proclamationMgrService.removeById(proclamationId);
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(Proclamation proclamation) {
	    	Proclamation old = this.proclamationMgrService.getById(proclamation.getProclamationId());
	    	old.setContent(proclamation.getContent());
	    	old.setIsTop(proclamation.getIsTop());
	    	old.setTitle(proclamation.getTitle());
	        this.proclamationMgrService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
}
