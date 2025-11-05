package ru.xing.springcourse.petproject3rest.kafka;

public interface MeasurementEventSender {
    void sendMeasurementEvent(Object event);
}
