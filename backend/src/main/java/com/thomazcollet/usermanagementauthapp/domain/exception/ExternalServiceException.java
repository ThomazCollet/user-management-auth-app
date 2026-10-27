package com.thomazcollet.usermanagementauthapp.domain.exception;

/**
 * Exceção lançada quando ocorre uma falha de comunicação ou indisponibilidade
 * em serviços externos (APIs de terceiros via OpenFeign, etc.).
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}