package com.calendarugr.auth_service.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarugr.auth_service.dtos.ErrorResponseDTO;
import com.calendarugr.auth_service.dtos.JwtResponseDTO;
import com.calendarugr.auth_service.dtos.LoginRequestDTO;
import com.calendarugr.auth_service.dtos.RefreshTokenRequestDTO;
import com.calendarugr.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<JwtResponseDTO> jwtResponse = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (jwtResponse.isEmpty()) {
            // Add a error message to the response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ErrorResponseDTO("NOT_FOUND", "El usuario no existe o la contraseña es incorrecta"));
        }
        return ResponseEntity.ok(jwtResponse.get());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO request) {
        Optional<JwtResponseDTO> jwtResponse = authService.refresh(request.getRefreshToken());

    if (jwtResponse.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("NotFound", "El token de refresco no es válido"));
    }

    return ResponseEntity.ok(jwtResponse.get());
    }
    
}
