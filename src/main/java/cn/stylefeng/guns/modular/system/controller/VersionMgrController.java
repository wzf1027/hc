package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.AppVersion;
import cn.stylefeng.guns.modular.system.service.VersionMgrService;
import cn.stylefeng.guns.modular.system.warpper.VersionWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;


@Controller
@RequestMapping("/versionMgr")
public class VersionMgrController extends BaseController {

	 private String PREFIX = "/modular/system/version/";

	    @Autowired
	    private VersionMgrService versionMgrService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "version.html";
	    }


	    @RequestMapping("/version_update/{versionId}")
	    public String noticeUpdate(@PathVariable Long versionId, Model model) {
	    	AppVersion version = this.versionMgrService.getById(versionId);
	        model.addAllAttributes(BeanUtil.beanToMap(version));
	        LogObjectHolder.me().set(version);
	        return PREFIX + "version_edit.html";
	    }



	    @RequestMapping(value = "/list")
	    @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.versionMgrService.list(condition);
	        Page<Map<String, Object>> wrap = new VersionWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }



	    @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(AppVersion version) {
	        if (ToolUtil.isOneEmpty(version, version.getAddress(),version.getVersionId(),version.getVersion())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        AppVersion old = this.versionMgrService.getById(version.getVersionId());
	        old.setAddress(version.getAddress());
	        old.setContent(version.getContent());
	        old.setVersion(version.getVersion());
	        this.versionMgrService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
}
