package ru.xing.springcourse.petproject3rest.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.models.Sensor;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;
import ru.xing.springcourse.petproject3rest.services.SensorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @GetMapping
    public List<SensorDTO> getAllSensors() {
        log.info("Getting all sensors");
        return sensorService.getAllSensors();
    }

    @GetMapping("/{name}")
    public SensorDTO getSensorByName(@PathVariable String name) {
        log.info("Getting sensor by name {}", name);
        return sensorService.getSensorByName(name);
    }

    @PostMapping("/register")
    public String addSensor(@RequestParam String name) {
        log.info("Adding sensor {}", name);
        sensorService.registerSensor(name);
        return "Sensor registered successfully";
    }
}
