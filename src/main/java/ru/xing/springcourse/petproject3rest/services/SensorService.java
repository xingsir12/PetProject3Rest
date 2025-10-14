package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.SensorMapper;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        log.info(sensor.toString());

        //До маппинга

//        List<MeasurementDTO> measurements = sensor.getMeasurement()
//                .stream()
//                .map(m -> new MeasurementDTO(
//                        m.getValue(),
//                        m.isRaining(),
//                        m.getMeasurementDateTime()
//                )).toList();

        //После
        return sensorMapper.toDTO(sensor);

    }

    //Получить список всех сенсоров
    public List<SensorDTO> getAllSensors() {
        //До маппинга
//        List<Sensor> sensors = sensorRepository.findAll();
//        return sensors.stream()
//                .map(sensor -> SensorDTO.builder()
//                        .name(sensor.getName())
//                        .measurements(sensor.getMeasurement() == null ? null:
//                                sensor.getMeasurement().stream()
//                                        .map(m -> new MeasurementDTO(
//                                                m.getValue(),
//                                                m.isRaining(),
//                                                m.getMeasurementDateTime()))
//                                        .toList())
//                        .build())
//                .toList();

        return sensorRepository.findAll()
                .stream()
                .map(sensorMapper::toDTO)
                .toList();
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
