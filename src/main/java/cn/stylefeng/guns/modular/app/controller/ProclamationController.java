package cn.stylefeng.guns.modular.app.controller;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.stylefeng.guns.modular.app.service.ProclamationService;
import cn.stylefeng.guns.modular.system.entity.Proclamation;
import cn.stylefeng.guns.core.util.ResponseData;

/**
 * 公告控制器
 */
@Controller
@RequestMapping("/app/proclamation/")
public class ProclamationController {
	
	@Resource
	private ProclamationService proclamationService;


	/**
	 * 公告列表数据
	 * @param isTop 是否首页显示，0表示否，1表示是,2表示全部
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @return ResponseData
	 */
	@PostMapping(value = "/getProclamationList")
	@ResponseBody
	public ResponseData getProclamationList(@RequestParam(defaultValue="0",required=false,value="isTop")Integer isTop
			,@RequestParam(defaultValue="0",required=false,value="pageSize")Integer pageSize,
			@RequestParam(defaultValue="0",required=false,value="pageNumber")Integer pageNumber) {
		return proclamationService.getProclamationList(isTop,pageSize,pageNumber);
	}
	
	/**
	 * 某个公告的详情内容
	 * @param id 公告id
	 * @param model  model
	 * @return String
	 */
	@PostMapping(value="/proclamationPage")
	public String proclamationPage(Long id,Model model) {
		model.addAttribute("createTime",null);
		model.addAttribute("title", null);
		model.addAttribute("content",null);
		Proclamation proclamation = proclamationService.getProclamationDetail(id);
		if(proclamation != null) {
			model.addAttribute("title", proclamation.getTitle());
			model.addAttribute("createTime",proclamation.getCreateTime());
			model.addAttribute("content",proclamation.getContent());
		}
		return "/modular/frame/proclamationDetail.html";
	}
    
	
}
