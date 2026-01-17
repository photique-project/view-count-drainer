package com.benchpress200.viewcountdrainer.exhibition.scheduler;

import com.benchpress200.viewcountdrainer.exhibition.service.ExhibitionViewCountIdleKeyCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionViewCountIdleKeyCleanupScheduler {
    private final ExhibitionViewCountIdleKeyCleanupService exhibitionViewCountIdleKeyCleanupService;

    @Scheduled(cron = "${spring.schedule.cleanup.cron}")
    public void cleanup() {
        long start = System.currentTimeMillis();

        try {
            int removed = exhibitionViewCountIdleKeyCleanupService.cleanup();
            long elapsed = System.currentTimeMillis() - start;

            log.info(
                    "Exhibition's empty view count cleanup finished. removed={}, elapsed={}ms",
                    removed,
                    elapsed
            );

        } catch (RedisSystemException e) {
            log.error("Redis failure during view count cleanup", e);
        }

    }

}
