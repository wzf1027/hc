package cn.stylefeng.guns.modular.app.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.app.service.AgreementAppService;

/**
 *  协议控制器
 * @author zf
 *
 */
@RestController
@RequestMapping("/app/agreement")
public class AgreementAppController {

	@Resource
	private AgreementAppService agreementAppService;
	
	/**
	 * 获取协议详情数据
	 * @return
	 */
	@RequestMapping(value = "/getAgreement")
	public ResponseData getAgreement() {
		return agreementAppService.getAgreement();
	}
	
}
