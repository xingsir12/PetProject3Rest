package ru.xing.springcourse.petproject3rest.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.models.MyUser;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeUsers();
    }

    private void initializeUsers() {
        // Удаляем всех существующих пользователей (для тестирования)
        userRepository.deleteAll();
        log.info("🗑️ Cleared existing users");

        // Создаем admin
        createUserIfNotExists("admin", "admin123", "ADMIN");

        // Создаем user
        createUserIfNotExists("user", "user123", "USER");

        // Создаем superadmin с двумя ролями
        createUserIfNotExists("superadmin", "super123", "USER,ADMIN");

        log.info("✅ Database initialization completed!");
        log.info("📋 Available users:");
        log.info("   - admin:admin123 (ADMIN)");
        log.info("   - user:user123 (USER)");
        log.info("   - superadmin:super123 (USER,ADMIN)");
    }

    private void createUserIfNotExists(String username, String rawPassword, String role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            MyUser user = new MyUser();
            user.setUsername(username);

            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            user.setRole(role);

            userRepository.save(user);

            log.info("✅ Created user: {} with role: {}", username, role);
            log.info("   Password hash: {}...", encodedPassword.substring(0, 20));
        } else {
            log.info("ℹ️ User already exists: {}", username);
        }
    }
}
