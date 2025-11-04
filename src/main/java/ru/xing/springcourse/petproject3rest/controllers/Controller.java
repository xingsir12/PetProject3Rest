package ru.xing.springcourse.petproject3rest.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.xing.springcourse.petproject3rest.dto.MeasurementEvent;
import ru.xing.springcourse.petproject3rest.kafka.KafkaProducer;

@RestController
public class Controller {

    private final KafkaProducer kafkaProducer;

    public Controller(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/kafka/send")
    public String sendMessage(@RequestBody MeasurementEvent event) {

        kafkaProducer.sendMeasurementEvent(event);

        return "Successfully sent message";
    }
}
