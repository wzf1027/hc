package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.WallterLogService;
import cn.stylefeng.guns.modular.system.warpper.WallterLogWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;


@Controller
@RequestMapping("/wallterLogMgr")
public class WallterLogController extends BaseController {


	 private String PREFIX = "/modular/system/wallterLog/";

	    @Autowired
	    private WallterLogService wallterLogService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "wallterLog.html";
	    }

	    @RequestMapping(value = "/list")
	    @ResponseBody
	    public Object list(@RequestParam(required = false) String phone,@RequestParam(required = false) String timeLimit) {
	    	//拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        Page<Map<String, Object>> list = this.wallterLogService.list(phone,beginTime,endTime);
	        Page<Map<String, Object>> wrap = new WallterLogWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }

	
}
