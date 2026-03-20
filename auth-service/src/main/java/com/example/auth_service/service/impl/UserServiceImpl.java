package com.example.auth_service.service.impl;


import com.example.auth_service.dto.UserDTO;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if(userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
    }

    @Override
    public Map<String, String> login(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtUtil.generateAccessToken(user.getUsername()));
        tokens.put("refreshToken", jwtUtil.generateRefreshToken(user.getUsername()));

        return tokens;
    }


    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            String username = jwtUtil.getUsernameFromToken(refreshToken);
            return jwtUtil.generateAccessToken(username);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
    }


}