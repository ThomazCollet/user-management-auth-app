package com.thomazcollet.usermanagementauthapp.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "O nome completo é obrigatório")
        String fullName,

        @NotBlank(message = "O telefone é obrigatório")
        String phone,

        @NotNull(message = "A data de nascimento é obrigatória")
        LocalDate birthDate
) {}