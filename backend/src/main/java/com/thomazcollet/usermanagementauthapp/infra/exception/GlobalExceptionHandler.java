package com.thomazcollet.usermanagementauthapp.infra.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException; // Importação essencial para o 404

import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ConflictException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ExternalServiceException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.dto.response.StandardError;
import com.thomazcollet.usermanagementauthapp.dto.response.StandardError.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        // Intercepta rotas inexistentes -> Retorna 404 limpo
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<StandardError> handleNoResourceFound(NoResourceFoundException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.NOT_FOUND;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Recurso Não Encontrado",
                                "O endereço solicitado não existe.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        // Intercepta métodos HTTP não suportados (ex: GET em rota que só aceita POST)
        // -> Retorna 405
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<StandardError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Método Não Permitido",
                                "O método HTTP " + ex.getMethod() + " não é suportado para esta rota.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        // Intercepta Content-Type incompatível -> Retorna 415
        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<StandardError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Tipo de Mídia Não Suportado",
                                "O tipo de conteúdo enviado não é suportado por este endpoint.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
                HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Regra de Negócio Violada",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        // Intercepta conflitos de negócio (ex: registro duplicado por concorrência) ->
        // Retorna 409
        @ExceptionHandler({ ConflictException.class, DataIntegrityViolationException.class })
        public ResponseEntity<StandardError> handleConflictException(Exception ex, HttpServletRequest request) {
                log.warn("Violação de unicidade ou conflito de dados [{}]: {}", request.getRequestURI(),
                                ex.getMessage());

                HttpStatus status = HttpStatus.CONFLICT;
                String message = ex instanceof ConflictException ? ex.getMessage()
                                : "Já existe um registro com os dados informados no sistema.";

                StandardError error = StandardError.simple(
                                status.value(),
                                "Conflito de Dados",
                                message,
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Erro de Autenticação",
                                "Credenciais inválidas ou usuário inativo.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<StandardError> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.FORBIDDEN;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Acesso Negado",
                                "Você não possui permissão para acessar este recurso.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(ExternalServiceException.class)
        public ResponseEntity<StandardError> handleExternalServiceException(ExternalServiceException ex,
                        HttpServletRequest request) {
                log.warn("Falha em serviço externo [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

                HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Serviço Externo Indisponível",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(feign.FeignException.class)
        public ResponseEntity<StandardError> handleFeignException(feign.FeignException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_GATEWAY;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Erro de Comunicação Externa",
                                "Serviço externo indisponível no momento. Tente novamente mais tarde.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Corpo da Requisição Inválido",
                                "O corpo da requisição está malformado ou contém um JSON inválido.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<StandardError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                String message = String.format("O parâmetro '%s' recebeu o valor '%s', que é de um tipo inválido.",
                                ex.getName(), ex.getValue());

                StandardError error = StandardError.simple(
                                status.value(),
                                "Parâmetro Inválido",
                                message,
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<StandardError> handleConstraintViolation(ConstraintViolationException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Erro de Validação",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }

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

        @ExceptionHandler(Exception.class)
        public ResponseEntity<StandardError> handleGenericException(Exception ex, HttpServletRequest request) {
                log.error("Erro interno não tratado [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                StandardError error = StandardError.simple(
                                status.value(),
                                "Erro Interno no Servidor",
                                "Ocorreu um erro inesperado no sistema. Entre em contato com o suporte.",
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }
}