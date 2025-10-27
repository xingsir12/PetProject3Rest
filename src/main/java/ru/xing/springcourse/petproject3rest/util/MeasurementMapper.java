package ru.xing.springcourse.petproject3rest.util;

import org.springframework.stereotype.Component;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;

import java.time.LocalDateTime;

@Component
public class MeasurementMapper {

    public MeasurementDTO toDTO(Measurement measurement) {
        return MeasurementDTO.builder()
                .value(measurement.getValue())
                .raining(measurement.isRaining())
                .measurementDateTime(measurement.getMeasurementDateTime())
                .build();
    }

    public Measurement toEntity(MeasurementDTO measurementDTO, Sensor sensor) {
        return Measurement.builder()
                .value(measurementDTO.getValue())
                .raining(measurementDTO.getRaining())
                .measurementDateTime(measurementDTO.getMeasurementDateTime() != null
                        ? measurementDTO.getMeasurementDateTime()
                        : LocalDateTime.now())
                .sensor(sensor)
                .build();
    }
}
