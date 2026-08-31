package com.thomazcollet.usermanagementauthapp.infra.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.dto.response.StandardError;
import com.thomazcollet.usermanagementauthapp.dto.response.StandardError.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError error = StandardError.simple(
                status.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422 Unprocessable Entity ou 400 Bad Request
        StandardError error = StandardError.simple(
                status.value(),
                "Regra de Negócio Violada",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    // Intercepta falhas de validação das anotações do Jakarta (@NotBlank, @CPF,
    // @Email) nos Controllers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new FieldErrorResponse(field.getField(), field.getDefaultMessage()))
                .toList();

        StandardError error = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "Erro de Validação nos Campos",
                "Um ou mais campos contêm valores inválidos",
                request.getRequestURI(),
                fieldErrors);
        return ResponseEntity.status(status).body(error);
    }

    // Fallback para exceções genéricas não tratadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError error = StandardError.simple(
                status.value(),
                "Erro Interno no Servidor",
                "Ocorreu um erro inesperado no sistema. Entre em contato com o suporte.",
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}