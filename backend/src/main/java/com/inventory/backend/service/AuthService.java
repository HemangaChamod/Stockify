package com.inventory.backend.service;

import com.inventory.backend.model.User;
import com.inventory.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // LOGIN
    public boolean login(String email, String password) {
        return userRepository
                .findByEmail(email)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    // REGISTER
    public boolean register(String email, String password) {

        if (userRepository.existsByEmail(email)) {
            return false;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // later we hash it

        userRepository.save(user);
        return true;
    }
}
