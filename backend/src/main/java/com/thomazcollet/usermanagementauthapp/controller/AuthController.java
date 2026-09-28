package com.thomazcollet.usermanagementauthapp.controller;

import com.thomazcollet.usermanagementauthapp.dto.request.LoginRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.RegisterUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
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
    private final AuthenticationManager authenticationManager; // Injeção do AuthenticationManager

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
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        // Cria o token de autenticação passando o 'login' (que pode ser email ou
        // username) e a 'password'
        var usernamePasswordToken = new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password());

        // O AuthenticationManager valida as credenciais usando o seu UserDetailsService
        Authentication authentication = authenticationManager.authenticate(usernamePasswordToken);

        // Se a autenticação passar, o usuário é considerado válido.
        // O próximo passo será gerar o Token JWT e retorná-lo para o cliente!

        return ResponseEntity.ok().build();
    }
}