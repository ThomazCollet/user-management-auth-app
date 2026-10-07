package com.thomazcollet.usermanagementauthapp.security;

import com.thomazcollet.usermanagementauthapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Recupera o token do header Authorization
        var token = recoverToken(request);

        if (token != null) {
            // 2. Valida o token e recupera o subject (email ou username) gravado nele
            String login = tokenService.validateToken(token);

            if (!login.isEmpty()) {
                // 3. Busca o usuário no banco de dados com base no login do token
                var user = userRepository.findByUsernameOrEmail(login, login)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário do token não encontrado"));

                // 4. Cria o objeto de autenticação do Spring Security usando o UserDetailsImpl
                var userDetails = new UserDetailsImpl(user);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                // 5. Salva a autenticação no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 6. Continua o fluxo da requisição para os próximos filtros/controllers
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " e retorna apenas o token puro
        }
        return null;
    }
}