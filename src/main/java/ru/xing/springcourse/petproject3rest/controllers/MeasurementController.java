package ru.xing.springcourse.petproject3rest.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.measurements")
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

    @GetMapping
    public MeasurementDTO getMeasurementById(int id) {
        log.info("Getting measurement for id {}", id);
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found"));

        return new MeasurementDTO(
                measurement.getValue(),
                measurement.isRaining(),
                measurement.getMeasurementDateTime()
        );
    }
}
