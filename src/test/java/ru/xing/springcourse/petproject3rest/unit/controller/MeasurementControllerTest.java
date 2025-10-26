package ru.xing.springcourse.petproject3rest.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.xing.springcourse.petproject3rest.config.SecurityConfig;
import ru.xing.springcourse.petproject3rest.controllers.MeasurementController;
import ru.xing.springcourse.petproject3rest.dto.MeasurementDTO;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;
import ru.xing.springcourse.petproject3rest.services.MeasurementService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MeasurementController.class)
@Import(SecurityConfig.class)
public class MeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeasurementService measurementService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnPagedMeasurements() throws Exception {
        // Arrange
        MeasurementDTO measurementDTO = MeasurementDTO.builder()
                .value(25.5)
                .isRaining(false)
                .measurementDateTime(LocalDateTime.now())
                .build();

        Page<MeasurementDTO> page = new PageImpl<> (List.of(measurementDTO));
        when(measurementService.getAllMeasurements(any(Pageable.class)))
                .thenReturn(page);

        // Act & assert
        mockMvc.perform(get("/api/measurements")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value").value(25.5))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAddMeasurement_withUserRole() throws Exception {
        // Arrange
        doNothing().when(measurementService)
                .addMeasurement(eq("TestSensor"), any(MeasurementDTO.class));

        mockMvc.perform(post("/api/measurements/add")
                .param("sensorName", "TestSensor")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\": 23.5, \"isRaining\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Measurement has been added successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAddMeasurement_WithAdminRole() throws Exception {
        // Arrange
        doNothing().when(measurementService)
                .addMeasurement(eq("TestSensor"), any(MeasurementDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/measurements/add")
                        .param("sensorName", "TestSensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": 23.5, \"isRaining\": false}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReject_AddMeasurement_WithoutAuth() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/measurements/add")
                        .param("sensorName", "TestSensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": 23.5, \"isRaining\": false}"))
                .andExpect(status().isUnauthorized()); // 401
    }
}
