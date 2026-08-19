package com.thomazcollet.usermanagementauthapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thomazcollet.usermanagementauthapp.domain.entity.Role;
import com.thomazcollet.usermanagementauthapp.domain.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}