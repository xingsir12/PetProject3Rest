package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MeasurementService {
    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;

    public void addMeasurement(String sensorName, MeasurementDTO measurementDTO) {
        Sensor sensor = sensorRepository.findByName(sensorName)
                .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorName));

        Measurement measurement = Measurement.builder()
                .value(measurementDTO.getValue())
                .raining(measurementDTO.isRaining())
                .measurementDateTime(measurementDTO.getMeasurementDateTime() != null
                        ? measurementDTO.getMeasurementDateTime()
                        : LocalDateTime.now())
                .sensor(sensor)
                .build();

        measurementRepository.save(measurement);

        log.info("Added measurement for sensor '{}': value = {}, raining = {} ",
                sensorName, measurementDTO.getValue(), measurementDTO.isRaining());
    }
}
