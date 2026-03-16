package com.example.auth_service.controller;

import com.example.auth_service.dto.ApiResponse;
import com.example.auth_service.dto.UserDTO;
import com.example.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin({"*"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserDTO userDTO) {

        userService.register(userDTO);

        return ResponseEntity.ok(
                new ApiResponse(
                        200,
                        "User registered successfully",
                        userDTO
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody UserDTO userDTO) {
        Map<String, String> tokens = userService.login(userDTO);
        return ResponseEntity.ok(
                new ApiResponse(200, "Login successful", tokens)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        String newAccessToken = userService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(
                new ApiResponse(200, "Token refreshed successfully", newAccessToken)
        );
    }

}