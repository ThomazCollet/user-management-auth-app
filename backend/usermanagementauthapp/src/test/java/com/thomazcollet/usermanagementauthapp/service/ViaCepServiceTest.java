package com.thomazcollet.usermanagementauthapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.infra.feign.ViaCepClient;
import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;

@ExtendWith(MockitoExtension.class)
class ViaCepServiceTest {

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private ViaCepService viaCepService;

    private ViaCepResponse validResponse;

    @BeforeEach
    void setUp() {
        validResponse = new ViaCepResponse(
                "01001-000", "Praça da Sé", "lado ímpar", "Sé", 
                "São Paulo", "SP", "São Paulo", "Sudoeste", 
                "3550308", "1004", "11", "7107", false
        );
    }

    @Test
    @DisplayName("Should sanitize zip code and return address response when zip code is valid")
    void givenValidZipCodeWithMask_whenFindAddressByZipCode_shouldSanitizeAndReturnAddress() {
        when(viaCepClient.getAddressByZipCode("01001000")).thenReturn(validResponse);

        ViaCepResponse response = viaCepService.findAddressByZipCode("01001-000");

        assertThat(response).isNotNull();
        assertThat(response.cep()).isEqualTo("01001-000");
        verify(viaCepClient).getAddressByZipCode("01001000");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw BusinessException when zip code is blank or empty")
    void givenBlankZipCode_whenFindAddressByZipCode_shouldThrowBusinessException(String invalidZipCode) {
        assertThatThrownBy(() -> viaCepService.findAddressByZipCode(invalidZipCode))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O CEP é obrigatório e não pode ser nulo ou vazio");

        verify(viaCepClient, never()).getAddressByZipCode(anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when zip code is null")
    void givenNullZipCode_whenFindAddressByZipCode_shouldThrowBusinessException() {
        assertThatThrownBy(() -> viaCepService.findAddressByZipCode(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O CEP é obrigatório e não pode ser nulo ou vazio");

        verify(viaCepClient, never()).getAddressByZipCode(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "ABCDEFGH"})
    @DisplayName("Should throw BusinessException when zip code does not have exactly 8 digits")
    void givenInvalidLengthZipCode_whenFindAddressByZipCode_shouldThrowBusinessException(String invalidZipCode) {
        assertThatThrownBy(() -> viaCepService.findAddressByZipCode(invalidZipCode))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O CEP deve conter exatamente 8 dígitos numéricos");

        verify(viaCepClient, never()).getAddressByZipCode(anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ViaCEP returns erro flag as true")
    void givenNonExistingZipCode_whenFindAddressByZipCode_shouldThrowResourceNotFoundException() {
        ViaCepResponse errorResponse = new ViaCepResponse(
                null, null, null, null, null, null, null, null, null, null, null, null, true
        );

        when(viaCepClient.getAddressByZipCode("99999999")).thenReturn(errorResponse);

        assertThatThrownBy(() -> viaCepService.findAddressByZipCode("99999999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("CEP não encontrado: 99999999");
    }
}