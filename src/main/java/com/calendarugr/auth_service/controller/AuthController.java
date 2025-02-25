package com.calendarugr.auth_service.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarugr.auth_service.models.JwtResponse;
import com.calendarugr.auth_service.models.LoginRequest;
import com.calendarugr.auth_service.models.RefreshTokenRequest;
import com.calendarugr.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<JwtResponse> jwtResponse = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (jwtResponse.isEmpty()) {
            // Add a error message to the response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "El usuario no fue encontrado" );
        }
        return ResponseEntity.ok(jwtResponse.get());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        Optional<JwtResponse> jwtResponse = authService.refresh(request.getRefreshToken());

    if (jwtResponse.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El token de refresco no es válido");
    }

    return ResponseEntity.ok(jwtResponse.get());
    }
    
}
