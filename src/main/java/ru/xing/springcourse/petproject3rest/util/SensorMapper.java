package ru.xing.springcourse.petproject3rest.util;

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
                .measurements(sensor.getMeasurement() == null
                    ? Collections.emptyList()
                        : sensor.getMeasurement().stream()
                            .map(measurementMapper::toDTO)
                        .toList())
                .build();
    }

    public Sensor toEntity(SensorDTO sensorDTO) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorDTO.getName());
        return sensor;
    }
}
