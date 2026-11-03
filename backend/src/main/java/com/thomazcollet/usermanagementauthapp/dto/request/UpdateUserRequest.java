package com.thomazcollet.usermanagementauthapp.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
                @NotBlank(message = "O nome completo é obrigatório") @Size(max = 100, message = "O nome completo não pode exceder 100 caracteres") String fullName,

                @Size(max = 20, message = "O telefone não pode exceder 20 caracteres") String phone,

                @NotNull(message = "A data de nascimento é obrigatória") @Past(message = "A data de nascimento deve ser uma data no passado") LocalDate birthDate) {
}
