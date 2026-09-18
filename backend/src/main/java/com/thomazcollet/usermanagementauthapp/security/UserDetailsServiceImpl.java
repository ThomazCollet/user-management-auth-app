package com.thomazcollet.usermanagementauthapp.security;

import com.thomazcollet.usermanagementauthapp.domain.entity.User;
import com.thomazcollet.usermanagementauthapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por carregar os dados do usuário do banco de dados
 * durante o processo de autenticação do Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Injeção de dependência via construtor
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco pelo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o username: " + username));

        // 2. Retorna a nossa classe Adapter (UserDetailsImpl) que empacota o User
        return new UserDetailsImpl(user);
    }
}