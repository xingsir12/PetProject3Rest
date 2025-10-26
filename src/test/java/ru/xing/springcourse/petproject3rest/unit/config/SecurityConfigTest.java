package ru.xing.springcourse.petproject3rest.unit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.xing.springcourse.petproject3rest.services.SensorService;
import ru.xing.springcourse.petproject3rest.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    @MockitoBean
    private UserService userService;

    @Test
    void publicEndpoints_AccessibleWithoutAuth() throws Exception {
        // Mock данные для публичных endpoints
        when(sensorService.getAllSensors(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_RequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userRole_CannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRole_CanAccessAdminEndpoints() throws Exception {
        when(userService.getAllUsers(any(org.springframework.data.domain.Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void authenticatedUser_CanAccessProtectedSensorEndpoints() throws Exception {
        when(sensorService.getAllSensors(any(org.springframework.data.domain.Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void adminWithBothRoles_CanAccessEverything() throws Exception {
        when(sensorService.getAllSensors(any())).thenReturn(Page.empty());
        when(userService.getAllUsers(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }
}
