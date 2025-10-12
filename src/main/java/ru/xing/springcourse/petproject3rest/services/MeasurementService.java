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
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.MeasurementMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementService {
    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;
    private final MeasurementMapper measurementMapper;

    //Добавить новое измерение
    @Transactional
    public void addMeasurement(String sensorName, MeasurementDTO measurementDTO) {
        Sensor sensor = sensorRepository.findByName(sensorName)
                .orElseThrow(() -> new BusinessException("Sensor not found: " + sensorName));

        //Без маппинга
//        Measurement measurement = Measurement.builder()
//                .value(measurementDTO.getValue())
//                .raining(measurementDTO.isRaining())
//                .measurementDateTime(measurementDTO.getMeasurementDateTime() != null
//                        ? measurementDTO.getMeasurementDateTime()
//                        : LocalDateTime.now())
//                .sensor(sensor)
//                .build();

        //С маппингом
        Measurement measurement = measurementMapper.toEntity(measurementDTO, sensor);

        measurementRepository.save(measurement);

        log.info("Added measurement for sensor '{}': value = {}, raining = {} ",
                sensorName, measurementDTO.getValue(), measurementDTO.isRaining());
    }

    //Получить список измерений
    @Transactional(readOnly = true)
    public List<MeasurementDTO> getAllMeasurements() {
//        return measurementRepository.findAll()
//                .stream()
//                .map(m -> new MeasurementDTO(
//                        m.getValue(),
//                        m.isRaining(),
//                        m.getMeasurementDateTime()))
//                .collect(Collectors.toList());

        return measurementRepository.findAll()
                .stream()
                .map(measurementMapper::toDTO)
                .toList();
    }

    //Получить по id измерение
    @Transactional(readOnly = true)
    public MeasurementDTO getMeasurementById(int id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found: " + id));

//        return new MeasurementDTO(
//                measurement.getValue(),
//                measurement.isRaining(),
//                measurement.getMeasurementDateTime()
//        );

        return measurementMapper.toDTO(measurement);
    }

    //Количество дождевых измерений
    @Transactional(readOnly = true)
    public long countRainingMeasurements() {
        long count = measurementRepository.countByRainingTrue();

        log.info("Number of raining measurements: {}", count);
        return count;
    }

    //Список всех дождевых измерений
    @Transactional(readOnly = true)
    public List<MeasurementDTO> getRainingMeasurements() {
        List<Measurement> measurements = measurementRepository.findByRainingTrue();

        log.info("List of raining measurements: {}", measurements.size());

//        return measurements.stream()
//                .map(m -> new MeasurementDTO(
//                        m.getValue(),
//                        m.isRaining(),
//                        m.getMeasurementDateTime()
//                )).toList();

        return measurements.stream()
                .map(measurementMapper::toDTO)
                .toList();
    }
}
