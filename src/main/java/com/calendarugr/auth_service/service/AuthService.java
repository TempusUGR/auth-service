package com.calendarugr.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.calendarugr.auth_service.PasswordUtil;
import com.calendarugr.auth_service.models.JwtResponse;
import com.calendarugr.auth_service.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class AuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String userServiceUrl = "http://localhost:8081/user";
    private final String SECRET_KEY = System.getProperty("JWT_SECRET");

    public Optional<JwtResponse> authenticate(String email, String password) {

        if (!checkUGREmail(email)) {
            return Optional.empty();
        }

        User user = null;

        try {
            System.out.println("Consultando al servicio de usuarios: " + userServiceUrl + "/email/" + email);

            user = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/email/{email}", email)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            System.out.println("Respuesta del servicio de usuarios: "
                    + (user != null ? user.toString() : "Usuario no encontrado"));

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
        String accessToken = generateToken(user.getNickname(), user.getRole().getName(), 86400000);
        // 1 week
        String refreshToken = generateToken(user.getNickname(), user.getRole().getName(), 604800000);

        return Optional.of(new JwtResponse(accessToken, refreshToken));

    }

    public Optional<JwtResponse> refresh(String refreshToken) {

        try {
            // Validar y extraer los claims del refresh token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String nickname = claims.getSubject();

            String role = claims.get("role", String.class);

            String newAccessToken = generateToken(nickname, role, 86400000);

            return Optional.of(new JwtResponse(newAccessToken, refreshToken));

        } catch (Exception e) {
            System.err.println("Error al refrescar el token: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String generateToken(String nickname, String role, long expiration) {

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(nickname)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Boolean checkUGREmail (String email) {
        return email.endsWith("@ugr.es");
    }

}
