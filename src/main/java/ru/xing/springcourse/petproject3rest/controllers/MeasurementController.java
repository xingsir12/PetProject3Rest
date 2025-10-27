package ru.xing.springcourse.petproject3rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;
import ru.xing.springcourse.petproject3rest.util.ErrorUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/measurements")
@Slf4j
public class MeasurementController {
    private final MeasurementService measurementService;

    @Operation(
            summary = "Add new measurement (User role required)",
            description = "Add a new weather measurement for a specific sensor. Requires USER or ADMIN role",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Measurement added successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or sensor not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - USER role required"
            )
    })
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> addMeasurement(
            @Parameter(description = "Name of the sensor", example = "Sensor_Home")
            @RequestParam String sensorName,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Measurement data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MeasurementDTO.class),
                            examples = @ExampleObject(
                                    name = "Measurement example",
                                    value = "{\"value\": 23.5, \"raining\": false}"
                            )
                    )
            )
            @RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.throwErrors(bindingResult);
        }
        log.info("Adding measurement for sensor {}", sensorName);
        measurementService.addMeasurement(sensorName, measurementDTO);
        return ResponseEntity.ok("Measurement has been added successfully");
    }


    @Operation(
            summary = "Get all measurements",
            description = "Returns a paginated list of all measurements sorted by date"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved measurements"
            )
    })
    //Пагинация
    @GetMapping
    public Page<MeasurementDTO> getAllMeasurements(@Parameter(description = "Page number (0-based)", example = "0")
                                                   @RequestParam(defaultValue = "0") int page,

                                                   @Parameter(description = "Number of items per page", example = "20")
                                                   @RequestParam(defaultValue = "20") int size,

                                                   @Parameter(description = "Sort by field",
                                                           example = "id",
                                                           schema = @Schema(allowableValues = {"id", "value", "raining", "createdAt"}))
                                                   @RequestParam(defaultValue = "id") String sort,

                                                   @Parameter(description = "Sort direction", example = "asc",
                                                           schema = @Schema(allowableValues = {"asc", "desc"}))
                                                   @RequestParam(defaultValue = "desc") String direction) {

        // Валидация параметров
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        // Создаем Pageable с валидацией
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        log.info("Getting all measurements with pagination: page {}, size {}, sort {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return measurementService.getAllMeasurements(pageable);
    }

    @Operation(
            summary = "Get measurement by id",
            description = "Returns a specific measurement by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Measurement found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Measurement not found"
            )
    })
    @GetMapping("/{id}")
    public MeasurementDTO getMeasurementById(@PathVariable("id") int id) {
        log.info("Getting measurement by id {}", id);
        return measurementService.getMeasurementById(id);
    }

    @Operation(
            summary = "Count a raining measurements",
            description = "Returns the total count of measurements where raining = true"
    )
    @GetMapping("/rainy-days/count")
    public Map<String, Long> countRainingMeasurements() {
        long count = measurementService.countRainingMeasurements();
        log.info("Raining measurement count {}", count);
        return Map.of("count", count);
    }

    @Operation(
            summary = "Get rainy measurements",
            description = "Return paginated list of measurements where raining = true"
    )
    @GetMapping("/rainy-days")
    public Page<MeasurementDTO> getRainingMeasurements(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort by field", example = "id")
            @RequestParam(defaultValue = "id") String sort,

            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        // Валидация параметров
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        // Безопасная сортировка
        List<String> allowedSortFields = Arrays.asList("id", "value", "raining", "createdAt");
        String safeSortField = allowedSortFields.contains(sort) ? sort : "id";

        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), safeSortField);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        return measurementService.getRainingMeasurements(pageable);
    }
}
