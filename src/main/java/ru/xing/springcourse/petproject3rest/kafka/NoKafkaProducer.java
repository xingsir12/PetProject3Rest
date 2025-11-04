package ru.xing.springcourse.petproject3rest.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnMissingBean(KafkaProducer.class)
public class NoKafkaProducer {

    public void sendMeasurementEvent(Object event) {
        log.info("Kafka disabled — skipping sendMeasurementEvent()");
    }
}
