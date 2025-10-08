package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
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
public class SensorService {
    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;

    public SensorDTO getSensorByName(String name) {
        Sensor sensor = sensorRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        List<MeasurementDTO> measurements = sensor.getMeasurement()
                .stream()
                .map(m -> new MeasurementDTO(
                        m.getValue(),
                        m.isRaining(),
                        m.getMeasurementDateTime()))
                .collect(Collectors.toList());

        return SensorDTO.builder()
                .name(sensor.getName())
                .measurements(measurements).build();
    }

    //Получить список всех сенсоров
    public List<SensorDTO> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors.stream()
                .map(sensor -> SensorDTO.builder()
                        .name(sensor.getName())
                        .measurements(sensor.getMeasurement() == null ? null:
                                sensor.getMeasurement().stream()
                                        .map(m -> new MeasurementDTO(
                                                m.getValue(),
                                                m.isRaining(),
                                                m.getMeasurementDateTime()))
                                        .toList())
                        .build())
                .toList();
    }

    public void addMeasurement(String sensorName, MeasurementDTO measurementDTO) {
        Sensor sensor = sensorRepository.findByName(sensorName)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        Measurement measurement = Measurement.builder()
                .value(measurementDTO.getValue())
                .raining(measurementDTO.isRaining())
                .measurementDateTime(measurementDTO.getMeasurementDateTime() != null
                        ? measurementDTO.getMeasurementDateTime()
                        : LocalDateTime.now())
                .sensor(sensor)
                .build();

        measurementRepository.save(measurement);
    }

    public void registerSensor(String name) {
        Sensor sensor = Sensor.builder()
                .name(name)
                .build();

        sensorRepository.save(sensor);
    }
}
