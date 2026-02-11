package com.benchpress200.viewcountdrainer.exhibition.repository;

import com.benchpress200.viewcountdrainer.common.exception.OutboxPayloadSerializationException;
import com.benchpress200.viewcountdrainer.common.payload.ViewCountPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ExhibitionViewCountRepository {
    private static final String AGGREGATE_TYPE = "exhibition";
    private static final String EVENT_TYPE = "viewCountUpdated";

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateViewCount(Long exhibitionId, Long viewCount) {
        jdbcTemplate.update(
                "UPDATE exhibitions SET view_count = view_count + ? WHERE id = ?",
                viewCount,
                exhibitionId
        );

        Long currentViewCount = jdbcTemplate.queryForObject(
                "SELECT view_count FROM exhibitions WHERE id = ?",
                Long.class,
                exhibitionId
        );

        ViewCountPayload viewCountPayload = new ViewCountPayload(exhibitionId, currentViewCount);
        JsonNode payload = objectMapper.valueToTree(viewCountPayload);

        jdbcTemplate.update(
                "INSERT INTO outbox_events (aggregate_type, aggregate_id, event_type, payload, created_at) VALUES (?, ?, ?, CAST(? AS JSON), ?)",
                AGGREGATE_TYPE,
                exhibitionId.toString(),
                EVENT_TYPE,
                payload.toString(),
                LocalDateTime.now()
        );
    }
}
