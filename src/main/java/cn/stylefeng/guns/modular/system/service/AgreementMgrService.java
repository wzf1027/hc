package cn.stylefeng.guns.modular.system.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.modular.system.entity.Agreement;
import cn.stylefeng.guns.modular.system.mapper.AgreementMgrMapper;

@Service
public class AgreementMgrService extends ServiceImpl<AgreementMgrMapper, Agreement> {
	
	@Resource
	private AgreementMgrMapper agreementMgrMapper;

	public Agreement getAgreementDetail() {
		return this.baseMapper.selectOne(null);
	}

}
