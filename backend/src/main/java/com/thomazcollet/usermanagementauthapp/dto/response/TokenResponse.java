package com.thomazcollet.usermanagementauthapp.dto.response;

public record TokenResponse(
        String token,
        String type, // Padrão "Bearer"
        Long expiresIn // Tempo de expiração em milissegundos ou segundos
) {
    public TokenResponse(String token, Long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}