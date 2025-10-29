package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.SensorMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SensorService {
    private final SensorRepository sensorRepository;
    private final SensorMapper sensorMapper;
    private final MeasurementRepository measurementRepository;

    public SensorDTO getSensorByName(String name) {
        // Используем метод с @EntityGraph
        Sensor sensor = sensorRepository.findWithMeasurementsByName(name)
                .orElseThrow(() -> new BusinessException("Sensor not found"));

        log.info("Found sensor '{}' with {} measurements",
                name, sensor.getMeasurements().size());
        return sensorMapper.toDTO(sensor);
    }

    // Получить список всех сенсоров с измерениями
    public Page<SensorDTO> getAllSensors(Pageable pageable) {
        // Используем метод с @EntityGraph
        Page<Sensor> sensors = sensorRepository.findAll(pageable);

        log.info("Retrieved {} sensors with measurements", sensors.getNumberOfElements());
        return sensors.map(sensorMapper::toDTO);
    }

    // Регистрация нового сенсора
    @Transactional
    public void registerSensor(String name) {
        if (sensorRepository.findByName(name).isPresent()) {
            throw new BusinessException("Sensor already exists: " + name);
        }

        Sensor sensor = new Sensor();
        sensor.setName(name);
        sensorRepository.save(sensor);

        log.info("Sensor registered: {}", name);
    }

    // Удаление сенсора
    @Transactional
    public void deleteSensor(String name) {
        Sensor sensor = sensorRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("Sensor not found"));

        sensorRepository.delete(sensor);

        log.info("Sensor deleted: {}", name);
    }
}
