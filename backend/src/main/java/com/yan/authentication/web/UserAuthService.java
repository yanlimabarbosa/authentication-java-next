package com.yan.authentication.web;

import com.yan.authentication.rbac.domain.User;
import com.yan.authentication.rbac.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public record UserData(UUID id, String email, boolean active, String passwordHash) {}

    public UserData authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new UserData(user.getId(), user.getEmail(), user.isActive(), user.getPasswordHash());
    }

    public UserData createUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return new UserData(savedUser.getId(), savedUser.getEmail(), savedUser.isActive(), savedUser.getPasswordHash());
    }

    public long getAccessMinutes() { return 15L; }
    public long getRefreshDays() { return 7L; }
}


