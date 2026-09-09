package com.thomazcollet.usermanagementauthapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "O CEP é obrigatório") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou apenas 8 números") String zipCode,

        @Size(max = 20, message = "O número não pode exceder 20 caracteres") String number,

        @Size(max = 100, message = "O complemento não pode exceder 100 caracteres") String complement) {
}