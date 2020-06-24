package cn.stylefeng.guns;

import cn.stylefeng.roses.core.config.WebAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SpringBoot方式启动类
 *
 */
@SpringBootApplication(exclude = {WebAutoConfiguration.class})
@EnableScheduling//开启定时任务
public class GunsApplication {

    private final static Logger logger = LoggerFactory.getLogger(GunsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GunsApplication.class, args);
        logger.info(GunsApplication.class.getSimpleName() + " is success!");
    }
}
