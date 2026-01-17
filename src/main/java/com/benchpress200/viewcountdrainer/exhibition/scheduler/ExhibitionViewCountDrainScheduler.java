package com.benchpress200.viewcountdrainer.exhibition.scheduler;

import com.benchpress200.viewcountdrainer.exhibition.service.ExhibitionViewCountDrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionViewCountDrainScheduler {
    private final ExhibitionViewCountDrainService exhibitionViewCountDrainService;

    @Scheduled(fixedDelayString = "${spring.schedule.drain.fixed-delay}")
    public void drain() {
        long start = System.currentTimeMillis();

        try {
            int processed = exhibitionViewCountDrainService.drain();
            long elapsed = System.currentTimeMillis() - start;

            log.info("Exhibition's view count drain. processed={}, elapsed={}ms",
                    processed,
                    elapsed
            );
        } catch (RedisSystemException e){
            log.error("Redis failure during view count drain", e);
        } catch (DataAccessException e) {
            log.error("Database failure during view count drain", e);
        }
    }
}
