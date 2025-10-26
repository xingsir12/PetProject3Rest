package ru.xing.springcourse.petproject3rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.services.SensorService;
import ru.xing.springcourse.petproject3rest.util.ErrorUtil;

@Slf4j
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Tag(name = "sensor", description = "Sensor management API") //swagger tag
public class SensorController {
    private final SensorService sensorService;

    @Operation(
            summary = "Get all sensors",
            description = "Return a paginated list of all registered list"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved sensors",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
            ))
    })
    @GetMapping
    public Page<SensorDTO> getAllSensors(@PageableDefault (size = 20, sort = "name", direction = Sort.Direction.ASC)
                                             Pageable pageable) {
        log.info("Getting all sensors with pagination");
        return sensorService.getAllSensors(pageable);
    }

    @Operation(
            summary = "Get sensor by name",
            description = "Return detailed information about a specific sensor including its measurements"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Sensor found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SensorDTO.class)
            )),
            @ApiResponse(responseCode = "400",
                    description = "Sensor not found")
    })
    @GetMapping("/{name}")
    public SensorDTO getSensorByName(@PathVariable String name) {
        log.info("Getting sensor by name {}", name);
        return sensorService.getSensorByName(name);
    }


    @Operation(
            summary = "Register new sensor (Admin only)",
            description = "Registered a new weather sensor. Registered ADMIN role.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input of sensor already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    // Универсальный endpoint — принимает и JSON, и form-data
    @PostMapping(value = "/register", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerSensor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Sensor registered data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SensorDTO.class),
                            examples = @ExampleObject(
                                    name = "Sensor example",
                                    value = "{\"name\": \"Sensor_Home\"}"
                            )
                    )
            )
            @RequestBody @Valid SensorDTO sensorDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.throwErrors(bindingResult);
        }

        log.info("Registering sensor: {}", sensorDTO.getName());
        sensorService.registerSensor(sensorDTO.getName());

        return ResponseEntity.ok("Sensor registered successfully");
    }
}
