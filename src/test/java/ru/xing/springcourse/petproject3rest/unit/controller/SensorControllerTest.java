package ru.xing.springcourse.petproject3rest.unit.controller;

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
import ru.xing.springcourse.petproject3rest.controllers.SensorController;
import ru.xing.springcourse.petproject3rest.dto.SensorDTO;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;
import ru.xing.springcourse.petproject3rest.services.SensorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = SensorController.class)
@Import(SecurityConfig.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SensorService sensorService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldRegisterSensor_WithAdminRole() throws Exception {
        doNothing().when(sensorService).registerSensor("TestSensor");

        mvc.perform(post("/api/sensors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSensor\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sensor registered successfully"));

    }

    @Test
    void shouldReject_RegisterSensor_WithoutAuth() throws Exception {

        mvc.perform(post("/api/sensor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType("{\"name\": \"TestSensor\"}"))
                .andExpect(status().isUnauthorized()); //401
    }

    @Test
    void shouldGetAllSensors_WithoutAuth() throws Exception {
        SensorDTO sensorDTO = new SensorDTO("Sensor1", null);
        Page<SensorDTO> page = new PageImpl<>(List.of(sensorDTO));

        when(sensorService.getAllSensors(any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/api/sensors")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Sensor1"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldRegisterSensor_WithBasicAuth() throws Exception {
        // Arrange
        doNothing().when(sensorService).registerSensor("TestSensor");

        // Act & Assert
        mvc.perform(post("/api/sensors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"TestSensor\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReject_RegisterSensor_WithUserRole() throws Exception {
        doNothing().when(sensorService).registerSensor("TestSensor");

        mvc.perform(post("/api/sensors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":  \"TestSensor\"}"))
                .andExpect(status().isForbidden()); //403
    }

    @Test
    void shouldGetSensorByName_Public() throws Exception {
        SensorDTO sensorDTO = new SensorDTO("TestSensor", null);

        when(sensorService.getSensorByName("TestSensor")).thenReturn(sensorDTO);

        mvc.perform(get("/api/sensors/TestSensor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestSensor"));
    }
}
