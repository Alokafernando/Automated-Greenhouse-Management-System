package com.example.auth_service.service;

import com.example.auth_service.dto.UserDTO;

import java.util.Map;

public interface UserService {
    void register(UserDTO userDTO);

    Map<String, String> login(UserDTO userDTO);

    String refreshAccessToken(String refreshToken);
}
