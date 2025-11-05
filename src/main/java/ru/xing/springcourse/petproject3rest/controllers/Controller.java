package ru.xing.springcourse.petproject3rest.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.xing.springcourse.petproject3rest.dto.MeasurementEvent;
import ru.xing.springcourse.petproject3rest.kafka.KafkaProducer;
import ru.xing.springcourse.petproject3rest.kafka.MeasurementEventSender;

@RestController
public class Controller {

    private final MeasurementEventSender eventSender;

    public Controller(MeasurementEventSender eventSender) {
        this.eventSender = eventSender;
    }

    @PostMapping("/kafka/send")
    public String sendMessage(@RequestBody MeasurementEvent event) {
        eventSender.sendMeasurementEvent(event);
        return "Successfully sent message (or skipped)";
    }
}
