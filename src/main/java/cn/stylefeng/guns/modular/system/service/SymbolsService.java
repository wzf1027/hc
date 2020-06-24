package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.util.ApiClient;
import cn.stylefeng.guns.core.util.RedisUtil;
import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.core.util.response.Kline;
import cn.stylefeng.guns.core.util.response.KlineResponse;
import cn.stylefeng.guns.modular.system.entity.Symbols;
import cn.stylefeng.guns.modular.system.mapper.SymbolsMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;


@Service
public class SymbolsService extends ServiceImpl<SymbolsMapper, Symbols> {

    private final Logger logger = LoggerFactory.getLogger(SymbolsService.class);

    static final String API_KEY = "40e0f1a6-a983d11f-19351346-7ec30";

    static final String API_SECRET = "8be8da24-a6454280-2edde4ee-85a8a";

    private static volatile ApiClient client;



    public static ApiClient getApiClient() {
        if (client == null) {
            synchronized (ApiClient.class) {
                if (client == null) {
                    client = new ApiClient(API_KEY, API_SECRET);
                }
            }
        }
        return client;
    }

    int number=0;
    int count =0;
    int codeNumber = 1;

    @Resource
    private RedisUtil redisUtils;



    public Page<Map<String, Object>> list(String condition) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition);
    }


    /**
     * 更新所有币种的最新行情数据
     */
    public void updateMarketsInfo() {
        boolean flag = true;
        Long nowTime = new Date().getTime()/1000;
        if(count==0) {
            redisUtils.del("accessTime","accessNumber");
            count++;
        }
        if(redisUtils.get("accessTime")==null) {
            redisUtils.set("accessTime", nowTime);
        }else {
            long time =0;
            if(redisUtils.get("accessTime")!=null) {
                time = nowTime -Long.valueOf(redisUtils.get("accessTime")+"");
            }
            if(time<30 && redisUtils.get("accessNumber") != null && Long.valueOf(redisUtils.get("accessNumber")+"") >30) {
                flag=false;
            }
            if(time>30 && redisUtils.get("accessNumber") != null && Long.valueOf(redisUtils.get("accessNumber")+"") >30) {
                number =0;
                redisUtils.set("accessTime", nowTime);
                redisUtils.set("accessNumber", number++);
                logger.debug("超过伍秒，重置,"+ redisUtils.get("accessNumber")+"------"+redisUtils.get("accessTime"));
            }

        }
        if(flag) {
            if(codeNumber <1) {
                List<Symbols>  symbolsList = this.list();
                for (Symbols symbols : symbolsList) {
                    redisUtils.del(symbols.getCode());
                }
                codeNumber++;
            }
            redisUtils.set("accessNumber",number++ );
            List<Symbols> symbolsList = this.list();
            for (Symbols symbols2 : symbolsList) {
                String[] symbol = symbols2.getCode().split("/");
                KlineResponse kline = 	getApiClient().kline(symbol[0].toLowerCase()+symbol[1].toLowerCase(), "1day", "1");
                if(kline != null) {
                    if("ok".equalsIgnoreCase(kline.getStatus())) {
                        List<Kline>  klines = (List<Kline>) kline.checkAndReturn();
                        if(klines.size() > 0) {
                            Kline klineObject = klines.get(0);
                            Map<String,Object> map = new HashMap<>();
                            float spread = klineObject.getClose()-klineObject.getOpen() ;
                            float rose =spread/klineObject.getOpen()*100;
                            map.put("symbol", symbols2.getCode());
                            map.put("open", klineObject.getOpen());
                            map.put("close",klineObject.getClose());
                            map.put("high", klineObject.getHigh());
                            map.put("low", klineObject.getLow());
                            map.put("rose", rose);
                            map.put("amount", klineObject.getAmount());
                            redisUtils.set(symbols2.getCode(), map);
                        }
                    }
                }
            }
        }
    }


    public ResponseData selectSymbolsList() {
        List<Symbols>  symbolsList = this.list();
        List<Object>  list = new ArrayList<>(symbolsList.size());
        for (Symbols symbols : symbolsList) {
            Object symbolsObject = redisUtils.get(symbols.getCode());
            if(symbolsObject != null) {
                list.add(symbolsObject);
            }
        }
        return ResponseData.success(list);
    }
}
