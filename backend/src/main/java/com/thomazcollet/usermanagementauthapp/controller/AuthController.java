package com.thomazcollet.usermanagementauthapp.controller;

import com.thomazcollet.usermanagementauthapp.domain.entity.User;
import com.thomazcollet.usermanagementauthapp.dto.request.LoginRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.RegisterUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.response.TokenResponse;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
import com.thomazcollet.usermanagementauthapp.security.TokenService;
import com.thomazcollet.usermanagementauthapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService; // 1. Injeção do TokenService

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        UserProfileResponse response = userService.registerUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/users/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        var usernamePasswordToken = new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password());

        // 1. O AuthenticationManager valida as credenciais
        Authentication authentication = authenticationManager.authenticate(usernamePasswordToken);

        // 2. Extrai o usuário do principal
        var userDetails = (com.thomazcollet.usermanagementauthapp.security.UserDetailsImpl) authentication
                .getPrincipal();
        User user = userDetails.getUser();

        // 3. Gera o token JWT
        String token = tokenService.generateToken(user);

        // 4. Retorna utilizando o tempo de expiração dinâmico fornecido pelo TokenService
        TokenResponse response = new TokenResponse(token, tokenService.getExpirationSeconds());

        return ResponseEntity.ok(response);
    }
}