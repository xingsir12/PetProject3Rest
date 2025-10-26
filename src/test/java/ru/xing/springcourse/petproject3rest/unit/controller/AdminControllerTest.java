package ru.xing.springcourse.petproject3rest.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.xing.springcourse.petproject3rest.config.SecurityConfig;
import ru.xing.springcourse.petproject3rest.controllers.AdminController;
import ru.xing.springcourse.petproject3rest.dto.UserDTO;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;
import ru.xing.springcourse.petproject3rest.services.UserService;
import ru.xing.springcourse.petproject3rest.util.UserAlreadyHasRoleException;
import ru.xing.springcourse.petproject3rest.util.UserNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    /**
     Обязательно к добавлению, чтобы работали конфиги
     **/

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    private UserDTO user;

    @BeforeEach
    void setUp(){
        user = UserDTO.builder()
                .username("testUser")
                .role(List.of("USER"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        List<UserDTO> userList = Arrays.asList(user);
        Page<UserDTO> userPage = new PageImpl<>(userList, PageRequest.of(0, 20), 1);

        when(userService.getAllUsers(any())).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_Forbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_Success() throws Exception {
        when(userService.getUserByUsername("testUser")).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteUserToAdmin_Success() throws Exception {
        UserDTO adminUser = UserDTO.builder()
                .username("testUser")
                .role(List.of("ADMIN"))
                .build();

        when(userService.promoteToAdmin("testUser")).thenReturn(adminUser);

        mockMvc.perform(post("/api/admin/users/testUser/promote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User promoted to ADMIN"))
                .andExpect(jsonPath("$.user.username").value("testUser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_BadRequest_WhenAlreadyAdmin() throws Exception {
        when(userService.promoteToAdmin("testUser"))
                .thenThrow(new UserAlreadyHasRoleException("User already has role ADMIN"));

        mockMvc.perform(post("/api/admin/users/testUser/promote"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void demoteFromAdmin_Success() throws Exception {
        when(userService.demoteFromAdmin("admin")).thenReturn(user);

        mockMvc.perform(post("/api/admin/users/admin/demote")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User demoted from ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoles_Success() throws Exception {
        UserDTO updatedUser = UserDTO.builder()
                .username("testUser")
                .role(List.of("USER", "ADMIN"))
                .build();

        when(userService.updateUserRoles("testUser", "USER,ADMIN")).thenReturn(updatedUser);

        mockMvc.perform(put("/api/admin/users/testUser/roles")
                        .param("role", "USER,ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User roles updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser("testUser");

        mockMvc.perform(delete("/api/admin/users/testUser")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found"))
                .when(userService).deleteUser("nonexistent");

        mockMvc.perform(delete("/api/admin/users/nonexistent"))
                .andExpect(status().isNotFound());
    }

}
