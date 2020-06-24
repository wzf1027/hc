package cn.stylefeng.guns.modular.app.job;


import cn.stylefeng.guns.modular.system.service.SymbolsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class SymbolsAndKlineTasks {


    @Resource
    private SymbolsService symbolsService;


    /**
     * 	 每秒获取每个币种最新行情数据
     */
    @Scheduled(cron="0/1 * * * * ? ")
    public void updateMarketsInfo() {
        symbolsService.updateMarketsInfo();
    }

}
