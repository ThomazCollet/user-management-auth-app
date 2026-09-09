package com.thomazcollet.usermanagementauthapp.dto.response;

public record AddressResponse(
        Long id,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state) {
}