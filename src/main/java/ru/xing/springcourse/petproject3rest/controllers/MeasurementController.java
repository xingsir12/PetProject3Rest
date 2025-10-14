package ru.xing.springcourse.petproject3rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;
import ru.xing.springcourse.petproject3rest.util.ErrorUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/measurements")
@Slf4j
public class MeasurementController {
    private final MeasurementService measurementService;

    @PostMapping("/add")
    public ResponseEntity<String> addMeasurement(@RequestParam String sensorName
    , @RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.throwErrors(bindingResult);
        }
        log.info("Adding measurement for sensor {}", sensorName);
        measurementService.addMeasurement(sensorName, measurementDTO);
        return ResponseEntity.ok("Measurement has been added successfully");
    }

    //Пагинация
    @GetMapping
    public Page<MeasurementDTO> getAllMeasurements(
            @PageableDefault (size = 20, sort = "measurementDateTime", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Getting all measurements with pagination: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return measurementService.getAllMeasurements(pageable);
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
    public Page<MeasurementDTO> getRainingMeasurements(@PageableDefault (size = 20, sort = "measurementDateTime", direction = Sort.Direction.DESC)
                                                       Pageable pageable) {
        log.info("Getting raining measurements with pagination");
        return measurementService.getRainingMeasurements(pageable);
    }
}
