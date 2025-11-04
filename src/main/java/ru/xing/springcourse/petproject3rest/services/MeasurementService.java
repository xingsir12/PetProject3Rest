package ru.xing.springcourse.petproject3rest.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.dto.MeasurementEvent;
import ru.xing.springcourse.petproject3rest.kafka.KafkaProducer;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.util.BusinessException;
import ru.xing.springcourse.petproject3rest.util.MeasurementMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class MeasurementService {
    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;
    private final MeasurementMapper measurementMapper;
    private final KafkaProducer kafkaProducer;

    public MeasurementService(SensorRepository sensorRepository,
                              MeasurementRepository measurementRepository,
                              MeasurementMapper measurementMapper,
                              @Autowired(required = false) KafkaProducer kafkaProducer) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
        this.measurementMapper = measurementMapper;
        this.kafkaProducer = kafkaProducer;
    }

    // Добавляем измерение
    @Transactional
    @CacheEvict(value = "rainyDaysCount", allEntries = true)
    public void addMeasurement(@NotBlank String sensorName, @Valid MeasurementDTO measurementDTO) {
        Sensor sensor = sensorRepository.findByName(sensorName)
                .orElseThrow(() -> new BusinessException("Sensor not found: " + sensorName));

        //С маппингом
        Measurement measurement = measurementMapper.toEntity(measurementDTO, sensor);

        measurementRepository.save(measurement);

        log.info("Added measurement for sensor '{}': value = {}, raining = {} ",
                sensorName, measurementDTO.getValue(), measurementDTO.getRaining());

        // Отправка в Kafka только если доступен
        if (kafkaProducer != null) {
            MeasurementEvent event = MeasurementEvent.builder()
                    .sensorName(sensorName)
                    .temperature(measurementDTO.getValue())
                    .isRaining(measurementDTO.getRaining())
                    .timestamp(LocalDateTime.now())
                    .build();
            kafkaProducer.sendMeasurementEvent(event);
        }

        log.info("Measurement added for sensor '{}': value = {}", sensorName, measurementDTO.getValue());

    }

    //Добавим пагинацию, чтобы проект мог обрабатывать огромное количество измерений без потери памяти
    public Page<MeasurementDTO> getAllMeasurements(Pageable pageable) {
        Page<Measurement> measurements = measurementRepository.findAll(pageable);

        return measurements.map(measurementMapper::toDTO);
    }

    public MeasurementDTO getMeasurementById(int id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Measurement not found: " + id));

        return measurementMapper.toDTO(measurement);
    }

    //Количество дождевых измерений.
    //Кешируем статистику (она редко меняется)
    @Cacheable(value = "rainyDaysCount")
    public long countRainingMeasurements() {
        long count = measurementRepository.countByRainingTrue();

        log.info("Number of raining measurements: {}", count);
        return count;
    }

    //Список всех дождевых измерений
    public Page<MeasurementDTO> getRainingMeasurements(Pageable pageable) {
        Page<Measurement> measurements = measurementRepository.findByRainingTrue(pageable);
        log.info("List of raining measurements: {}", measurements.getTotalElements());

        return measurements.map(measurementMapper::toDTO);
    }
}
