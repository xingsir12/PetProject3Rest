package ru.xing.springcourse.petproject3rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.models.Measurement;
import ru.xing.springcourse.petproject3rest.repositories.MeasurementRepository;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;
import ru.xing.springcourse.petproject3rest.util.ErrorUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/measurements")
@Slf4j
public class MeasurementController {
    private final MeasurementService measurementService;

    @PostMapping("/{sensor}")
    public ResponseEntity<String> addMeasurement(@PathVariable("sensor") String sensorName
    , @RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.returnErrorsToClient(bindingResult);
        }
        log.info("Adding measurement for sensor {}", sensorName);
        measurementService.addMeasurement(sensorName, measurementDTO);
        return ResponseEntity.ok("Measurement has been added successfully");
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
