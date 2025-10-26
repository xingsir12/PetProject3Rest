package ru.xing.springcourse.petproject3rest.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.xing.springcourse.petproject3rest.dto.UserDTO;
import ru.xing.springcourse.petproject3rest.models.MyUser;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;
import ru.xing.springcourse.petproject3rest.services.UserService;
import ru.xing.springcourse.petproject3rest.util.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsServiceAutoConfiguration userDetailsServiceAutoConfiguration;

    @InjectMocks
    private UserService userService;

    private MyUser user;

    @BeforeEach
    void setUp() {
        user = new MyUser();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(new ArrayList<>(List.of("USER")));
    }

    @Test
    void getAllUsers_ReturnsPageOfUsers() {
        List<MyUser> userList = Arrays.asList(user);
        Page<MyUser> userPage = new PageImpl<>(userList);
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllUsers_EmptyPage() {
        Page<MyUser> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getRole().size());
        assertTrue(result.getRole().contains("USER"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_ThrowsException_WhenNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserByUsername("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void promoteToAdmin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);

        UserDTO result = userService.promoteToAdmin("testuser");

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());

        MyUser savedUser = userCaptor.getValue();
        assertTrue(savedUser.getRole().contains("ADMIN"));
        assertEquals(2, savedUser.getRole().size());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void promoteToAdmin_ThrowsException_WhenAlreadyAdmin() {
        user.setRole(new ArrayList<>(List.of("USER", "ADMIN")));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.promoteToAdmin("admin"));

        assertEquals("You are already an admin", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("admin");
        verify(userRepository, never()).save(any());
    }

    @Test
    void promoteToAdmin_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.promoteToAdmin("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void demoteFromAdmin_Success() {
        user.setRole(new ArrayList<>(List.of("USER", "ADMIN")));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);

        UserDTO result = userService.demoteFromAdmin("admin");

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());

        MyUser savedUser = userCaptor.getValue();
        assertFalse(savedUser.getRole().contains("ADMIN"));
        assertTrue(savedUser.getRole().contains("USER"));
        assertEquals(1, savedUser.getRole().size());
    }

    @Test
    void demoteFromAdmin_ThrowsException_WhenNotAdmin() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.demoteFromAdmin("testuser"));

        assertEquals("You are not an admin", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void demoteFromAdmin_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.demoteFromAdmin("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void updateUserRoles_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);

        UserDTO result = userService.updateUserRoles("testuser", "USER,ADMIN");

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());

        MyUser savedUser = userCaptor.getValue();
        assertEquals(2, savedUser.getRole().size());
        assertTrue(savedUser.getRole().contains("ROLE_USER"));
        assertTrue(savedUser.getRole().contains("ROLE_ADMIN"));
    }

    @Test
    void updateUserRoles_AddsRolePrefix_WhenMissing() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);

        userService.updateUserRoles("testuser", "USER,ADMIN");

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());

        MyUser savedUser = userCaptor.getValue();
        assertTrue(savedUser.getRole().stream().allMatch(r -> r.startsWith("ROLE_")));
    }

    @Test
    void updateUserRoles_HandlesSpacesInInput() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);

        userService.updateUserRoles("testuser", " USER , ADMIN ");

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());

        MyUser savedUser = userCaptor.getValue();
        assertEquals(2, savedUser.getRole().size());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser("testuser");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_ThrowsException_WhenNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.deleteUser("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }
}
