package cn.stylefeng.guns.modular.system.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.stylefeng.guns.modular.system.entity.SellerTimeSetting;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.SellerWallter;
import cn.stylefeng.guns.modular.system.mapper.SellerSysMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class SellerSysService extends ServiceImpl<SellerSysMapper, Seller> {

	@Resource
	private SellerSysMapper sellerSysMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition, String phone, String beginTime, String endTime, Integer isAccepter, String recommend, Integer isAuth, Integer enabled) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,phone,beginTime,endTime,isAccepter,recommend,isAuth,enabled);
	}

	public ResponseData updateAuth(Long sellerId, Integer status) {
		Seller seller = this.baseMapper.selectById(sellerId);
		if(seller != null) {
			if(seller.getIsAuth() != null && seller.getIsAuth() ==1) {
				if(status ==1) {
					seller.setIsAuth(2);
				}else {
					seller.setIsAuth(0);
					seller.setIdCardFront("");
					seller.setIdCardImage("");
					seller.setIdCardNo("");
					seller.setIdCardReverse("");
				}
				this.baseMapper.updateById(seller);
				return ResponseData.success(200, "审核成功", null);
			}
		}
		return ResponseData.error("审核失败");
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> wallterList(Long sellerId, Integer type) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.wallterList(page, sellerId,type);
	}

	public SellerWallter findSellerWallter(Long sellerWallterId) {
		return this.baseMapper.findSellerWallter(sellerWallterId);
	}

	public void updateSellerWallter(SellerWallter old) {
		 this.baseMapper.updteSellerWallter(old);
	}


    public SellerTimeSetting getSellerTimeSetting() {
		return this.baseMapper.getSellerTimeSetting();
    }

	public void updateSellerTimeSetting(SellerTimeSetting setting) {
		this.baseMapper.updateSellerTimeSetting(setting);
	}

    public List<Map<String, Object>> selectListTreeByCondition(String condition) {
		return this.baseMapper.selectListTreeByCondition(condition);
    }
}
