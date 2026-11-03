package com.thomazcollet.usermanagementauthapp.security;

import com.thomazcollet.usermanagementauthapp.domain.entity.User;
import com.thomazcollet.usermanagementauthapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 1. Limpa espaços extras
        String login = identifier != null ? identifier.trim() : "";

        // 2. Busca o usuário considerando o username exato e o e-mail em minúsculas
        // (normalizado)
        User user = userRepository.findByUsernameOrEmail(login, login.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + identifier));

        return new UserDetailsImpl(user);
    }
}