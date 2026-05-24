package com.xianyupaidan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xianyupaidan.entity.AuditLog;
import com.xianyupaidan.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogConsumer {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "xianyupaidan-audit", groupId = "xianyupaidan-audit-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            JsonNode root = objectMapper.readTree(record.value());

            if (root.path("isDdl").asBoolean(false)) {
                return;
            }

            String table = root.path("table").asText();
            String type = root.path("type").asText();
            JsonNode dataArray = root.path("data");
            JsonNode oldArray = root.path("old");

            if (!dataArray.isArray()) {
                return;
            }

            for (int i = 0; i < dataArray.size(); i++) {
                JsonNode dataRow = dataArray.get(i);
                JsonNode oldRow = (i < oldArray.size()) ? oldArray.get(i) : null;

                String recordId = extractRecordId(root.path("pkNames"), dataRow, oldRow);

                AuditLog logEntry = new AuditLog();
                logEntry.setTableName(table);
                logEntry.setOperationType(type);
                logEntry.setRecordId(recordId);
                logEntry.setOldData(oldRow != null && !oldRow.isNull() ? oldRow.toString() : null);
                logEntry.setNewData(dataRow != null && !dataRow.isNull() ? dataRow.toString() : null);
                auditLogMapper.insert(logEntry);
            }
        } catch (Exception e) {
            log.error("Failed to process audit message", e);
        }
    }

    private String extractRecordId(JsonNode pkNames, JsonNode dataRow, JsonNode oldRow) {
        if (pkNames == null || pkNames.isEmpty()) {
            return null;
        }
        JsonNode source = (dataRow != null && !dataRow.isNull()) ? dataRow : oldRow;
        if (source == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (JsonNode pk : pkNames) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }
            JsonNode value = source.path(pk.asText());
            sb.append(value.isMissingNode() ? "?" : value.asText());
        }
        return sb.toString();
    }
}
