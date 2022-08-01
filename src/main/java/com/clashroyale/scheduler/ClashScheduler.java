package com.clashroyale.scheduler;

import com.clashroyale.service.ClashService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClashScheduler {

    private final ClashService clashService;

    public ClashScheduler (final ClashService clashService) {
        this.clashService = clashService;
    }

    @Scheduled(cron = "0 40 09 * * ?")
    public void sendWarAttackDefaultersToDiscord() {
        clashService.sendDefaultersToDiscord();
    }

}
