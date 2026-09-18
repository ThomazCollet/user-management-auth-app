package com.thomazcollet.usermanagementauthapp.security;

import com.thomazcollet.usermanagementauthapp.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapter (Wrapper) que implementa UserDetails do Spring Security,
 * adaptando a nossa entidade de domínio User para o framework.
 */
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // Método utilitário caso precise recuperar a entidade original no sistema
    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // <-- Adicionado o .name() aqui!
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Aqui você pode decidir se usa o username ou o email para login.
        // Como o padrão costuma ser o username, retornamos ele:
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Se não tiver lógica de expiração de conta, retorna true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Se não tiver trava por tentativas falhas, retorna true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Se a senha não expira por tempo, retorna true
    }

    @Override
    public boolean isEnabled() {
        // Usamos o campo isActive do seu User para saber se a conta está ativa
        return user.getIsActive();
    }
}