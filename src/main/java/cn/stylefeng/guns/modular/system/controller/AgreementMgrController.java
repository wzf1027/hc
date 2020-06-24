package cn.stylefeng.guns.modular.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.modular.system.entity.Agreement;
import cn.stylefeng.guns.modular.system.service.AgreementMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;


@Controller
@RequestMapping("/agreementMgr")
public class AgreementMgrController extends BaseController {

	 private String PREFIX = "/modular/system/agreement/";

	    @Autowired
	    private AgreementMgrService agreementMgrService;


		@RequestMapping("")
		public String index() {
		    return PREFIX + "agreement.html";
		}
	  

		
	    @RequestMapping("/getAgreementDetail")
	    @ResponseBody
	    public ResponseData getAgreementDetail() {
	        Agreement agreement = agreementMgrService.getAgreementDetail();
	        return ResponseData.success(agreement);
	    }



	    @RequestMapping(value = "/saveOrUpdate")
	    @ResponseBody
	    public Object saveOrUpdate(Agreement agreement) {
	        if (ToolUtil.isOneEmpty(agreement, agreement.getContent())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(agreement.getAgreementId()!= null && agreement.getAgreementId() >0) {
	        	Agreement  old = this.agreementMgrService.getById(agreement.getAgreementId());
	        	old.setContent(agreement.getContent());
	 	        this.agreementMgrService.updateById(old);
	        }else {
	        	this.agreementMgrService.save(agreement);
	        }
	        return SUCCESS_TIP;
	    }
	
}
