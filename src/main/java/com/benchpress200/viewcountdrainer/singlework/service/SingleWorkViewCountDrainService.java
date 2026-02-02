package com.benchpress200.viewcountdrainer.singlework.service;

import com.benchpress200.viewcountdrainer.singlework.repository.SingleWorkViewCountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SingleWorkViewCountDrainService {
    private static final String SINGLEWORK_VIEW_COUNT_KEY = "singlework:view:*";
    private static final int SCAN_BUCKET_COUNT = 1000;
    private static final int PROCESSED_COUNT_INIT = 0;
    private static final long VIEW_COUNT_INIT = 0L;

    private final RedisTemplate<String, Long> redisTemplate;
    private final SingleWorkViewCountRepository singleWorkViewCountRepository;

    public int drain() {
        int processed = PROCESSED_COUNT_INIT; // 조회수 -> DB 반영한 행 카운팅

        ScanOptions options = ScanOptions.scanOptions()
                .match(SINGLEWORK_VIEW_COUNT_KEY) // 단일작품 조회수 키 매칭
                .count(SCAN_BUCKET_COUNT) // 한 번에 처리할 작업량 대략적인 힌트 제공
                .build();

        // 작업 완료 후 연결 세션 정리
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                Long viewCount = redisTemplate.opsForValue().getAndSet(key, VIEW_COUNT_INIT); // 현재 viewCount 가져오고 0으로 리셋

                if (viewCount == null || viewCount == VIEW_COUNT_INIT) {
                    continue;
                }

                Long singleWorkId = extractId(key);

                // DB 반영
                singleWorkViewCountRepository.updateViewCount(singleWorkId, viewCount);

                processed++;
            }
        }

        return processed;
    }

    private Long extractId(String key) {
        // singlework:view:{id}
        int idx = key.lastIndexOf(':');
        String id = key.substring(idx + 1);

        return Long.parseLong(id);
    }
}
