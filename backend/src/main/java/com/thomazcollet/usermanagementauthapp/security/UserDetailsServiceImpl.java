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
        // Busca o usuário no banco verificando se o 'identifier' bate com o username OU
        // com o email
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + identifier));

        return new UserDetailsImpl(user);
    }
}