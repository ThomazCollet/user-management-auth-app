package com.thomazcollet.usermanagementauthapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thomazcollet.usermanagementauthapp.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}