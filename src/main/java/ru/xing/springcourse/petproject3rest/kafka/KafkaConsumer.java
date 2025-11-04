package ru.xing.springcourse.petproject3rest.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.xing.springcourse.petproject3rest.dto.MeasurementEvent;

@Slf4j
@Service
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    public KafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "course", groupId = "weather-group")
    public void listen(String message) {
        try {
            MeasurementEvent event = objectMapper.readValue(message, MeasurementEvent.class);

            log.info("Received measurement event: sensor={}, temp={}, raining={}",
                    event.getSensorName(),
                    event.getTemperature(),
                    event.getIsRaining());

            if (event.getTemperature() > 30) {
                log.warn("High temperature alert: {}", event.getTemperature());
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message", e);
        }
    }
}
