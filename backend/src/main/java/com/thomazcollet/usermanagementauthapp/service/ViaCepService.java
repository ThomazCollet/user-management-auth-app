package com.thomazcollet.usermanagementauthapp.service;

import org.springframework.stereotype.Service;

import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.infra.feign.ViaCepClient;
import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepResponse findAddressByZipCode(String rawZipCode) {
        String zipCode = sanitizeAndValidateZipCode(rawZipCode);

        ViaCepResponse response = viaCepClient.getAddressByZipCode(zipCode);

        if (response != null && Boolean.TRUE.equals(response.erro())) {
            throw new ResourceNotFoundException("CEP não encontrado: " + rawZipCode);
        }

        return response;
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