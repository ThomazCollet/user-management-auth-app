package com.thomazcollet.usermanagementauthapp.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record UserProfileResponse(
        Long id,
        String fullName,
        String cpf,
        String email,
        String username,
        String phone,
        LocalDate birthDate,
        Boolean isEmailVerified,
        AddressResponse address,
        Set<String> roles) {
}