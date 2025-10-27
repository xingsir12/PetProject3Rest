package ru.xing.springcourse.petproject3rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<SensorDTO> getAllSensors(@Parameter(description = "Page number (0-based)", example = "0")
                                             @RequestParam(defaultValue = "0") int page,

                                         @Parameter(description = "Number of items per page", example = "20")
                                             @RequestParam(defaultValue = "20") int size,

                                         @Parameter(description = "Sort by field", example = "name")
                                             @RequestParam(defaultValue = "name") String sort,

                                         @Parameter(description = "Sort direction", example = "asc",
                                                 schema = @Schema(allowableValues = {"asc", "desc"}))
                                             @RequestParam(defaultValue = "asc") String direction) {

        // Валидация параметров
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        // Создаем Pageable с валидацией
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        log.info("Getting all sensors with pagination: page={}, size={}, sort={}, direction={}",
                page, size, sort, direction);

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
