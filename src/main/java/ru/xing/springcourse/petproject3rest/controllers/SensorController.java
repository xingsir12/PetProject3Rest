package ru.xing.springcourse.petproject3rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.services.SensorService;
import ru.xing.springcourse.petproject3rest.util.ErrorUtil;
import ru.xing.springcourse.petproject3rest.util.SensorMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;
    private final SensorMapper sensorMapper;

    @GetMapping
    public Page<SensorDTO> getAllSensors(@PageableDefault (size = 20, sort = "name", direction = Sort.Direction.ASC)
                                             Pageable pageable) {
        log.info("Getting all sensors with pagination");
        return sensorService.getAllSensors(pageable);
    }

    @GetMapping("/{name}")
    public SensorDTO getSensorByName(@PathVariable String name) {
        log.info("Getting sensor by name {}", name);
        return sensorService.getSensorByName(name);
    }

    // Универсальный endpoint — принимает и JSON, и form-data
    @PostMapping(value = "/register", consumes = {"application/json", "application/x-www-form-urlencoded"})
    public ResponseEntity<String> registerSensor(@RequestBody @Valid SensorDTO sensorDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.throwErrors(bindingResult);
        }

        log.info("Registering sensor: {}", sensorDTO.getName());
        sensorService.registerSensor(sensorDTO.getName());

        return ResponseEntity.ok("Sensor registered successfully");
    }
}
