package cn.stylefeng.guns.modular.app.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.app.mapper.AgreementAppMapper;
import cn.stylefeng.guns.modular.system.entity.Agreement;

@Service
public class AgreementAppService {

	@Resource
	private AgreementAppMapper agreementAppMapper;

	public ResponseData getAgreement() {
		Map<String, Object>  map = new HashMap<String, Object>();
		map.put("content", null);
		Agreement agreement = agreementAppMapper.getAgreement();
		if(agreement != null) {
			map.put("content", agreement.getContent());
		}
		return ResponseData.success(200, null, map);
	}
}
