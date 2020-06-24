package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.SellerPayMethod;
import cn.stylefeng.guns.modular.system.mapper.SellerPayMethodMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class SellerPayMethodService extends ServiceImpl<SellerPayMethodMapper, SellerPayMethod> {


	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String account, String phone, String payMethodName, Integer payMethodType, String nickName, Integer isCheck) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, account,phone,payMethodName,payMethodType,nickName,isCheck);
	}

    public Integer onLineNumberByType(Integer type) {
		return this.baseMapper.onLineNumberByType(type);
    }
}
