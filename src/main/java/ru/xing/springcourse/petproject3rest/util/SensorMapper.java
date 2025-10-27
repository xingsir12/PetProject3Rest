package ru.xing.springcourse.petproject3rest.util;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.util.Collections;

@Component
public class SensorMapper {
    private final MeasurementMapper measurementMapper;

    public SensorMapper(MeasurementMapper measurementMapper) {
        this.measurementMapper = measurementMapper;
    }

    public SensorDTO toDTO(Sensor sensor) {
        return SensorDTO.builder()
                .name(sensor.getName())
                .measurements(sensor.getMeasurements() != null &&
                        Hibernate.isInitialized(sensor.getMeasurements())
                ? sensor.getMeasurements().stream()
                        .map(measurementMapper::toDTO)
                        .toList()
                        : Collections.emptyList())
                .build();
    }

    public Sensor toEntity(SensorDTO sensorDTO) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorDTO.getName());
        return sensor;
    }
}
