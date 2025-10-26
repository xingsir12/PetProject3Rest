package ru.xing.springcourse.petproject3rest.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.xing.springcourse.petproject3rest.models.MyUser;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Создаем админа
            MyUser admin = new MyUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(List.of("ROLE_ADMIN"));
            userRepository.save(admin);

            // Создаем обычного пользователя
            MyUser user = new MyUser();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(List.of("ROLE_USER"));
            userRepository.save(user);

            // Создаем супер-админа
            MyUser superAdmin = new MyUser();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("super123"));
            superAdmin.setRole(List.of("ROLE_USER", "ROLE_ADMIN"));
            userRepository.save(superAdmin);

            System.out.println("Test users created successfully!");
        }
    }
}
