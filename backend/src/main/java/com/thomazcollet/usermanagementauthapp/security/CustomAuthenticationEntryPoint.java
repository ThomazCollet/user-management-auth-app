package com.thomazcollet.usermanagementauthapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomazcollet.usermanagementauthapp.dto.response.StandardError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        HttpStatus status = HttpStatus.UNAUTHORIZED; // HTTP 401

        StandardError error = StandardError.simple(
                status.value(),
                "Não Autorizado",
                "Token de autenticação ausente, expirado ou inválido.",
                request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Para serializar datas do Instant corretamente se houver
        mapper.writeValue(response.getWriter(), error);
    }
}