package com.thomazcollet.usermanagementauthapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thomazcollet.usermanagementauthapp.domain.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}