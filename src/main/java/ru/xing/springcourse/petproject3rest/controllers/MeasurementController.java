package ru.xing.springcourse.petproject3rest.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.measurements")
@Slf4j
public class MeasurementController {
    private final MeasurementService measurementService;

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
    public MeasurementDTO getMeasurementById(@PathVariable("sensors") String sensorName) {
        log.info("Getting measurement for sensor {}", sensorName);
        measurementService.getAllMeasurements();
    }
}
