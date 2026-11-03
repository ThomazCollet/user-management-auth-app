package com.thomazcollet.usermanagementauthapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
                @NotBlank(message = "A senha atual é obrigatória") String currentPassword,

                @NotBlank(message = "A nova senha é obrigatória") @Size(min = 8, max = 50, message = "A nova senha deve ter entre 8 e 50 caracteres") String newPassword) {
}