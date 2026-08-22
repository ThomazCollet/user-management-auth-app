package com.thomazcollet.usermanagementauthapp.service;

import org.springframework.stereotype.Service;

import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.infrastructure.feign.ViaCepClient;
import com.thomazcollet.usermanagementauthapp.infrastructure.feign.dto.ViaCepResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepResponse findAddressByZipCode(String rawZipCode) {
        if (rawZipCode == null) {
            throw new IllegalArgumentException("O CEP não pode ser nulo");
        }

        // Remove hífens, pontos, espaços ou qualquer caractere que não seja dígito
        String sanitizedZipCode = rawZipCode.replaceAll("\\D", "");

        if (sanitizedZipCode.length() != 8) {
            throw new IllegalArgumentException("O CEP deve conter exatamente 8 dígitos numéricos");
        }

        ViaCepResponse response = viaCepClient.getAddressByZipCode(sanitizedZipCode);

        // A API do ViaCEP não lança HTTP 404 quando o CEP não existe; ela devolve o
        // JSON {"erro": "true"}
        if (response != null && Boolean.TRUE.equals(response.erro())) {
            throw new ResourceNotFoundException("CEP não encontrado: " + rawZipCode);
        }

        return response;
    }
}