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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MeasurementService {
    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;

    //Добавить новое измерение
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

    //Получить список измерений
    public List<MeasurementDTO> getAllMeasurements() {
        return measurementRepository.findAll()
                .stream()
                .map(m -> new MeasurementDTO(
                        m.getValue(),
                        m.isRaining(),
                        m.getMeasurementDateTime()))
                .collect(Collectors.toList());
    }

    //Получить по id измерение
    public MeasurementDTO getMeasurementById(int id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found: " + id));

        return new MeasurementDTO(
                measurement.getValue(),
                measurement.isRaining(),
                measurement.getMeasurementDateTime()
        );
    }

    private long countRainingMeasurements() {
        long count = measurementRepository.findAll().stream()
                .filter(Measurement::isRaining)
                .count();

        log.info("Number of raining measurements: {}", count);
        return count;
    }
}
