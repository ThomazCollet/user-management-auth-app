package com.thomazcollet.usermanagementauthapp.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record UpdateUserRequest(
        @NotBlank(message = "O nome completo é obrigatório")
        String fullName,

        String phone,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        LocalDate birthDate
) {}