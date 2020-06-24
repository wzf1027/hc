package cn.stylefeng.guns.modular.system.service;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.TeamBonusSetting;
import cn.stylefeng.guns.modular.system.mapper.TeamBonusSettingMapper;

@Service
public class TeamBonusSettingService extends ServiceImpl<TeamBonusSettingMapper, TeamBonusSetting> {

	@Resource
	private TeamBonusSettingMapper teamBonusSettingMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition);
	}

	public TeamBonusSetting getLastTeamBonusSettingOne() {
		return this.teamBonusSettingMapper.getLastTeamBonusSettingOne();
	}

	public void updateSettingById(TeamBonusSetting setting) {
		this.baseMapper.updateSettingById(setting);
	}
	
}
