package com.ruoyi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

// 启动时就加载
@RestController
@Slf4j
public class FixedPrintTask {
    static {
        log.info("FixedPrintTask init");
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void execute() {
        log.info("TASK EXEC!");
    }
}