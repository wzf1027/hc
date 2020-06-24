package cn.stylefeng.guns.modular.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.stylefeng.guns.core.util.PageUtils;
import cn.stylefeng.guns.core.util.Query;
import cn.stylefeng.guns.modular.app.mapper.ProclamationMapper;
import cn.stylefeng.guns.modular.system.entity.Proclamation;
import cn.stylefeng.guns.core.util.ResponseData;


@Service
public class ProclamationService {

	@Resource
	private ProclamationMapper proclamationMapper;

	public ResponseData getProclamationList(Integer isTop, Integer pageSize, Integer pageNumber) {
		Map<String, Object> params = new HashMap<>();
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
       if (isTop <2){
		   params.put("isTop",isTop);
	   }
        Query query = new Query(params);
		List<Proclamation>  proclamationList = proclamationMapper.selectProclamationList(query);
		List<Map<String,Object>>  list =null;
		int total = 0;
		if(proclamationList != null && proclamationList.size() >0) {
			list = new ArrayList<>();
			for (Proclamation proclamation : proclamationList) {
				Map<String,Object> map = new HashMap<>();
				map.put("title", proclamation.getTitle());//标题
				map.put("id", proclamation.getProclamationId());//id
				map.put("introduce",proclamation.getIntroduce());//简介
				map.put("content",proclamation.getContent());//内容
				map.put("createTime", proclamation.getCreateTime().getTime()/1000);
				list.add(map);
			}
			 total = proclamationMapper.selectProclamationListCount(query); 
		}
		 PageUtils pageUtils = new PageUtils(total, list);
		 return ResponseData.success(pageUtils);
	}

	public Proclamation getProclamationDetail(Long id) {
		Map<String, Object> params = new HashMap<>();
        params.put("pageNumber", 0);
        params.put("pageSize", 0);
        params.put("proclamationId",id);
        Query query = new Query(params);
		List<Proclamation>  list = proclamationMapper.selectProclamationList(query);
		if(list != null && list.size() >0) {
			return list.get(0);
		}
		return null;
	}
	

}
