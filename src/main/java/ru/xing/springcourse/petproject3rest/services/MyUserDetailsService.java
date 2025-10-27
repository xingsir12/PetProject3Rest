package ru.xing.springcourse.petproject3rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.xing.springcourse.petproject3rest.config.MyUserDetails;
import ru.xing.springcourse.petproject3rest.models.MyUser;
import ru.xing.springcourse.petproject3rest.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRole().stream()
                .map(role -> {
                    // Очищаем роль от лишних символов
                    String cleanedRole = role.replace("\"", "").replace("[", "").replace("]", "").trim();
                    // Добавляем префикс ROLE_ если его нет
                    if (!cleanedRole.startsWith("ROLE_")) {
                        cleanedRole = "ROLE_" + cleanedRole;
                    }
                    return new SimpleGrantedAuthority(cleanedRole);
                })
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
