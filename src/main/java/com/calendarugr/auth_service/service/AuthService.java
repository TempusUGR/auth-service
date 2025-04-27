package com.calendarugr.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.calendarugr.auth_service.config.PasswordUtil;
import com.calendarugr.auth_service.dtos.JwtResponseDTO;
import com.calendarugr.auth_service.dtos.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class AuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    String url = "http://user-service/user";

    private final String SECRET_KEY = System.getProperty("JWT_SECRET");

    public Optional<JwtResponseDTO> authenticate(String email, String password) {

        if (!checkUGREmail(email)) {
            System.out.println("Email no pertenece a la UGR");
            return Optional.empty();
        }

        UserDTO user = null;

        try {

            user = webClientBuilder.build()
                    .get()
                    .uri(url + "/email/{email}", email)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        if (user == null) {
            return Optional.empty();
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            return Optional.empty();
        }

        // 1 day
        String accessToken = generateToken(user.getId().toString(), user.getRole().getName(), 86400000);
        // 1 week
        String refreshToken = generateToken(user.getId().toString(), user.getRole().getName(), 604800000);

        return Optional.of(new JwtResponseDTO(accessToken, refreshToken));

    }

    public Optional<JwtResponseDTO> refresh(String refreshToken) {

        try {
            // Validar y extraer los claims del refresh token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String id = claims.getSubject();

            String role = claims.get("role", String.class);

            String newAccessToken = generateToken(id, role, 86400000);

            return Optional.of(new JwtResponseDTO(newAccessToken, refreshToken));

        } catch (Exception e) {
            System.err.println("Error al refrescar el token: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String generateToken(String id, String role, long expiration) {

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
        .setHeaderParam("typ", "JWT")
                .setSubject(id.toString())
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Boolean checkUGREmail (String email) {
        return ( email.endsWith("@ugr.es") || email.endsWith("@correo.ugr.es") );
    }

}
