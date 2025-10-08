package ru.xing.springcourse.petproject3rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.repositories.SensorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorRepository sensorRepository;

    public List<SensorDTO> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(sensor -> SensorDTO.builder()
                        .name(sensor.getName())
                        .build())
                .toList();
    }
}
