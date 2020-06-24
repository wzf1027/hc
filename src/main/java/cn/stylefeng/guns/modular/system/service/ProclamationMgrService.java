package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.Proclamation;
import cn.stylefeng.guns.modular.system.mapper.ProclamationMgrMapper;

@Service
public class ProclamationMgrService extends ServiceImpl<ProclamationMgrMapper, Proclamation> {
	
	@Resource
	private ProclamationMgrMapper proclamationMgrMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition) {
		  Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.list(page, condition);
	}


}
