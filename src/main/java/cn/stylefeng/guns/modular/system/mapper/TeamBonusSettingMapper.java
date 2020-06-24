package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.TeamBonusSetting;

public interface TeamBonusSettingMapper extends BaseMapper<TeamBonusSetting> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(Page page, String condition);

	TeamBonusSetting getLastTeamBonusSettingOne();

	void updateSettingById(TeamBonusSetting setting);

	
}
