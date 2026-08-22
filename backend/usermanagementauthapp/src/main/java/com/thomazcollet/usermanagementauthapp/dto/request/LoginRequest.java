package com.thomazcollet.usermanagementauthapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Informe o username ou e-mail") String login,

        @NotBlank(message = "A senha é obrigatória") String password) {
}