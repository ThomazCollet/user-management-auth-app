package com.thomazcollet.usermanagementauthapp.dto.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<FieldErrorResponse> errors // Útil para erros de validação do Jakarta Bean Validation (@Valid)
) {
    public record FieldErrorResponse(String field, String message) {
    }

    // Construtor utilitário para erros simples sem lista de campos
    public static StandardError simple(Integer status, String error, String message, String path) {
        return new StandardError(Instant.now(), status, error, message, path, null);
    }
}