package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.dto.UserDTO;
import ru.xing.springcourse.petproject3rest.models.MyUser;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;
import ru.xing.springcourse.petproject3rest.util.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final MyUserDetailsService myUserDetailsService;

    /**
     * Получить всех пользователей с пагинацией
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<MyUser> users = userRepository.findAll(pageable);
        return users.map(this::toDTO);
    }

    /**
     * Получить пользователя по username
     */
    public UserDTO getUserByUsername(String username) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));

        return toDTO(user);
    }

    /**
     * Повысить пользователя до ADMIN
     */
    @Transactional
    public UserDTO promoteToAdmin(String username) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));

        if (user.getRole().contains("ADMIN")) {
            throw new BusinessException("You are already an admin");
        }

        List<String> newRoles = new ArrayList<>(user.getRole());
        newRoles.add("ADMIN");
        user.setRole(newRoles);

        MyUser saved = userRepository.save(user);
        log.info("User {} promoted to ADMIN. New role: {}", username, newRoles);

        return toDTO(saved);
    }

    /**
     * Понизить пользователя с ADMIN до USER
     */
    @Transactional
    public UserDTO demoteFromAdmin(String username) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));

        if (!user.getRole().contains("ADMIN")) {
            throw new BusinessException("You are not an admin");
        }

        List<String> newRole = user.getRole().stream()
                        .filter(role -> !role.equals("ADMIN"))
                                .collect(Collectors.toList());

        user.setRole(newRole);

        MyUser saved = userRepository.save(user);
        log.info("User {} demoted from ADMIN. New role: {}", username, newRole);

        return toDTO(saved);
    }

    /**
     * Изменить роли пользователя напрямую
     */
    @Transactional
    public UserDTO updateUserRoles(String username, String rolesString) {
        log.info("Updating roles for user {}: {}", username, rolesString);

        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Парсим строку ролей
        List<String> newRoles = Arrays.stream(rolesString.split(","))
                .map(String::trim)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());

        user.setRole(newRoles);

        MyUser savedUser = userRepository.save(user);
        log.info("Roles updated for user {} successfully", username);

        return toDTO(user);
    }

    /**
     * Удалить пользователя
     */

    @Transactional
    public void deleteUser(String username) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));

        userRepository.delete(user);
        log.info("User {} deleted", username);
    }

    /**
     * Конвертация Entity → DTO
     */
    private UserDTO toDTO(MyUser myUser) {
        return UserDTO.builder()
                .username(myUser.getUsername())
                .role(myUser.getRole())
                .build();
    }
}
