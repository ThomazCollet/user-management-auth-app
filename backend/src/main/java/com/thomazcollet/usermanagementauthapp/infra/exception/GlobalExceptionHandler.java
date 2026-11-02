package com.thomazcollet.usermanagementauthapp.infra.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

        // Intercepta conflitos de negócio (ex: registro duplicado por concorrência) -> Retorna 409
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

        // Intercepta falhas de autenticação (senha errada, usuário inativo, etc.) -> Retorna 401
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

        // Intercepta tentativas de acesso a recursos sem a devida permissão/role -> Retorna 403
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

        // Intercepta falhas de comunicação com serviços externos (ex: ViaCEP fora do ar) -> Retorna 503
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

        // Intercepta falhas genéricas do Feign caso ocorram fora dos services mapeados -> Retorna 502
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

        // Intercepta JSON malformado ou ilegível -> Retorna 400
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

        // Intercepta erros de tipo de parâmetro (ex: passar string em ID numérico) -> Retorna 400
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

        // Intercepta violações de restrições em parâmetros de URL/método -> Retorna 400
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

        // Intercepta falhas de validação das anotações do Jakarta nos Controllers -> Retorna 400
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

        // Fallback para exceções genéricas não tratadas (agora com log.error completo) -> Retorna 500
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
