package ru.xing.springcourse.petproject3rest.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.xing.springcourse.petproject3rest.dto.MeasurementEvent;

@Service
@Slf4j
@ConditionalOnProperty(value = "spring.kafka.bootstrap-servers")
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMeasurementEvent(MeasurementEvent event) {
        try{
            String message = objectMapper.writeValueAsString(event);
            log.info("Sending to Kafka topic 'course': {}", message);
            kafkaTemplate.send("course", message);
            log.info("Sent to kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to send measurement event", e);
        }
    }
}
