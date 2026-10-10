package com.thomazcollet.usermanagementauthapp.service;

import org.springframework.stereotype.Service;

import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.infra.feign.ViaCepClient;
import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepResponse findAddressByZipCode(String rawZipCode) {
        String zipCode = sanitizeAndValidateZipCode(rawZipCode);

        try {
            ViaCepResponse response = viaCepClient.getAddressByZipCode(zipCode);

            // Se o ViaCEP retornar um objeto nulo ou com a flag erro = true
            if (response == null || Boolean.TRUE.equals(response.erro())) {
                throw new ResourceNotFoundException("CEP não encontrado: " + rawZipCode);
            }

            return response;
        } catch (FeignException e) {
            // Trata quedas ou instabilidades da API externa do ViaCEP (Retorna 503 ou
            // BusinessException)
            throw new BusinessException(
                    "Serviço de consulta de CEP indisponível no momento. Tente novamente mais tarde.");
        }
    }

    private String sanitizeAndValidateZipCode(String rawZipCode) {
        if (rawZipCode == null || rawZipCode.isBlank()) {
            throw new BusinessException("O CEP é obrigatório e não pode ser nulo ou vazio");
        }

        String sanitized = rawZipCode.replaceAll("\\D", "");

        if (sanitized.length() != 8) {
            throw new BusinessException("O CEP deve conter exatamente 8 dígitos numéricos");
        }

        return sanitized;
    }
}