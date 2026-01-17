package com.benchpress200.viewcountdrainer.singlework.scheduler;


import com.benchpress200.viewcountdrainer.singlework.service.SingleWorkViewCountDrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleWorkViewCountDrainScheduler {
    private final SingleWorkViewCountDrainService singleWorkViewCountDrainService;

    @Scheduled(fixedDelayString = "${spring.schedule.interval}")
    public void drain() {
        long start = System.currentTimeMillis();

        try {
            int processed = singleWorkViewCountDrainService.drain();
            long elapsed = System.currentTimeMillis() - start;

            log.info("Singlework's view count drain. processed={}, elapsed={}ms",
                    processed,
                    elapsed
            );
        } catch (Exception e) {
            log.error("Singlework's view count drain failed", e);
        }
    }
}
