package com.thomazcollet.usermanagementauthapp.controller;

import com.thomazcollet.usermanagementauthapp.dto.request.LoginRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.RegisterUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
import com.thomazcollet.usermanagementauthapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        UserProfileResponse response = userService.registerUser(request);

        // Gera a URI do recurso recém-criado (ex: /api/v1/users/{id} ou mantida no
        // contexto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/users/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        // Lógica de login/JWT será inserida aqui em breve
        return ResponseEntity.ok().build();
    }
}