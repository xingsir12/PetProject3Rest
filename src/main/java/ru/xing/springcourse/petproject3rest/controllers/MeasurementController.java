package ru.xing.springcourse.petproject3rest.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/measurements")
@Slf4j
public class MeasurementController {
    private final MeasurementService measurementService;
    private final MeasurementRepository measurementRepository;

    @PostMapping("/{sensors}")
    public String addMeasurement(@PathVariable("sensors") String sensorName
    , @RequestBody MeasurementDTO measurementDTO) {
        log.info("Adding measurement for sensor {}", sensorName);
        measurementService.addMeasurement(sensorName, measurementDTO);
        return "success";
    }

    @GetMapping
    public List<MeasurementDTO> getAllMeasurements() {
        log.info("Getting all measurements");
        return measurementService.getAllMeasurements();
    }

    @GetMapping("/{id}")
    public MeasurementDTO getMeasurementById(@PathVariable("id") int id) {
        log.info("Getting measurement by id {}", id);
        return measurementService.getMeasurementById(id);
    }

    @GetMapping("/raining/count")
    public Map<String, Long> countRainingMeasurements() {
        long count = measurementService.countRainingMeasurements();
        log.info("Raining measurement count {}", count);
        return Map.of("count", count);
    }

    @GetMapping("/raining")
    public List<MeasurementDTO> getRainingMeasurements() {
        log.info("Getting raining measurements");
        return measurementService.getRainingMeasurements();
    }
}
