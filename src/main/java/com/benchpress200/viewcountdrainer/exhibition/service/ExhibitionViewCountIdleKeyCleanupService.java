package com.benchpress200.viewcountdrainer.exhibition.service;

import com.benchpress200.viewcountdrainer.infrastructure.redis.lua.LuaScript;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExhibitionViewCountIdleKeyCleanupService {
    private static final String EXHIBITION_VIEW_COUNT_KEY = "exhibition:view:*";
    private static final int SCAN_BUCKET_COUNT = 1000;
    private static final int PROCESSED_COUNT_INIT = 0;
    private static final long EMPTY = 0L;
    private static final long FAIL = 0L;

    private final RedisTemplate<String, Long> redisTemplate;

    public int cleanup() {
        int processed = PROCESSED_COUNT_INIT;

        ScanOptions options = ScanOptions.scanOptions()
                .match(EXHIBITION_VIEW_COUNT_KEY) // 단일작품 조회수 키 매칭
                .count(SCAN_BUCKET_COUNT) // 한 번에 처리할 작업량 대략적인 힌트 제공
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();

                Long deleted = redisTemplate.execute( // 값이 0이라면 키 삭제
                        LuaScript.DELETE_IF_ZERO,
                        List.of(key),
                        String.valueOf(EMPTY)
                );

                if (deleted != null && deleted > FAIL) {
                    processed++;
                }
            }
        }

        return processed;
    }
}
