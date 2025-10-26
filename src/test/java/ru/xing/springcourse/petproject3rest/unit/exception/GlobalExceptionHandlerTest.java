package ru.xing.springcourse.petproject3rest.unit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.xing.springcourse.petproject3rest.controllers.SensorController;
import ru.xing.springcourse.petproject3rest.services.SensorService;
import ru.xing.springcourse.petproject3rest.util.BusinessException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    @Test
    @WithMockUser
    void handleBusinessException_Returns400() throws Exception {
        when(sensorService.getSensorByName("NonExistent"))
                .thenThrow(new BusinessException("Sensor not found"));

        mockMvc.perform(get("/api/sensors/NonExistent"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sensor not found"));
    }

    @Test
    @WithMockUser
    void handleBusinessException_ContainsTimestamp() throws Exception {
        when(sensorService.getSensorByName("Error"))
                .thenThrow(new BusinessException("Test error"));

        mockMvc.perform(get("/api/sensors/Error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    void handleBusinessException_ContainsStatus() throws Exception {
        when(sensorService.getSensorByName("Error"))
                .thenThrow(new BusinessException("Test error"));

        mockMvc.perform(get("/api/sensors/Error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
