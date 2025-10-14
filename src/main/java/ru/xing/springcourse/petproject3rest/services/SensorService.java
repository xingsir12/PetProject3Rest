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
        Sensor sensor = sensorRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("Sensor not found"));

        log.info(sensor.toString());
        return sensorMapper.toDTO(sensor);

    }

    //Получить список всех сенсоров
    public Page<SensorDTO> getAllSensors(Pageable pageable) {
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        return sensors.map(sensorMapper::toDTO);
    }

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
}
