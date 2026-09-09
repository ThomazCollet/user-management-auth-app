package com.thomazcollet.usermanagementauthapp.dto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "O nome completo é obrigatório") @Size(max = 100, message = "O nome completo não pode exceder 100 caracteres") String fullName,

        @NotBlank(message = "O CPF é obrigatório") @CPF(message = "O CPF informado é inválido") String cpf,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail informado é inválido") @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres") String email,

        @NotBlank(message = "O nome de usuário é obrigatório") @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres") String username,

        @NotBlank(message = "A senha é obrigatória") @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres") String password,

        @Size(max = 20, message = "O telefone não pode exceder 20 caracteres") String phone,

        @NotNull(message = "A data de nascimento é obrigatória") @Past(message = "A data de nascimento deve ser uma data no passado") LocalDate birthDate,

        @NotNull(message = "Os dados de endereço são obrigatórios") @Valid // Dispara a validação interna do
                                                                           // AddressRequest
        AddressRequest address) {
}