package cn.stylefeng.guns.modular.app.mapper;

import java.util.List;

import cn.stylefeng.guns.core.util.Query;
import cn.stylefeng.guns.modular.system.entity.Proclamation;

public interface ProclamationMapper {

	List<Proclamation> selectProclamationList(Query query);

	int selectProclamationListCount(Query query);


}
