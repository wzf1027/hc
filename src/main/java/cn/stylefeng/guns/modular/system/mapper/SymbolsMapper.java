package cn.stylefeng.guns.modular.system.mapper;


import cn.stylefeng.guns.modular.system.entity.Symbols;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.util.Map;


public interface SymbolsMapper extends BaseMapper<Symbols> {



    Page<Map<String, Object>> list(@Param("page") Page page,@Param("condition") String condition);
}
