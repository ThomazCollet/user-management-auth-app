package com.thomazcollet.usermanagementauthapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.thomazcollet.usermanagementauthapp.domain.entity.Address;
import com.thomazcollet.usermanagementauthapp.domain.entity.Role;
import com.thomazcollet.usermanagementauthapp.domain.entity.User;
import com.thomazcollet.usermanagementauthapp.domain.enums.RoleName;
import com.thomazcollet.usermanagementauthapp.domain.exception.BusinessException;
import com.thomazcollet.usermanagementauthapp.domain.exception.ResourceNotFoundException;
import com.thomazcollet.usermanagementauthapp.dto.request.AddressRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.RegisterUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.UpdatePasswordRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.UpdateUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;
import com.thomazcollet.usermanagementauthapp.repository.RoleRepository;
import com.thomazcollet.usermanagementauthapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ViaCepService viaCepService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role defaultRole;
    private ViaCepResponse viaCepResponse;
    private Address address;

    @BeforeEach
    void setUp() {
        defaultRole = new Role(1L, RoleName.ROLE_USER);

        address = Address.builder()
                .id(10L)
                .zipCode("01001-000")
                .street("Praça da Sé")
                .number("100")
                .neighborhood("Sé")
                .city("São Paulo")
                .state("SP")
                .build();

        user = User.builder()
                .id(1L)
                .fullName("Thomaz Collet")
                .cpf("12345678901")
                .email("thomaz@email.com")
                .username("thomazc")
                .password("encoded_pass")
                .phone("11999999999")
                .birthDate(LocalDate.of(2000, 1, 1))
                .isEmailVerified(false)
                .address(address)
                .roles(Set.of(defaultRole))
                .build();

        viaCepResponse = new ViaCepResponse(
                "01001-000", // cep
                "Praça da Sé", // logradouro
                "lado ímpar", // complemento
                "Sé", // bairro
                "São Paulo", // localidade
                "SP", // uf
                "São Paulo", // estado
                "Sudoeste", // regioes
                "3550308", // ibge
                "1004", // gia
                "11", // ddd
                "7107", // siafi
                false // erro
        );
    }

    @Nested
    @DisplayName("Tests for registerUser")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully when request is valid")
        void givenValidRequest_whenRegisterUser_shouldReturnUserProfileResponse() {
            AddressRequest addressReq = new AddressRequest("01001-000", "100", null);
            RegisterUserRequest request = new RegisterUserRequest(
                    "Thomaz Collet", "12345678901", "thomaz@email.com",
                    "thomazc", "secret123", "11999999999",
                    LocalDate.of(2000, 1, 1), addressReq);

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.existsByUsername(request.username())).thenReturn(false);
            when(userRepository.existsByCpf(request.cpf())).thenReturn(false);
            when(viaCepService.findAddressByZipCode(addressReq.zipCode())).thenReturn(viaCepResponse);
            when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(defaultRole));
            when(passwordEncoder.encode(request.password())).thenReturn("encoded_pass");
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserProfileResponse response = userService.registerUser(request);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo(request.email());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when email is already registered")
        void givenExistingEmail_whenRegisterUser_shouldThrowBusinessException() {
            RegisterUserRequest request = new RegisterUserRequest(
                    "Thomaz Collet", "12345678901", "thomaz@email.com",
                    "thomazc", "secret123", "11999999999",
                    LocalDate.of(2000, 1, 1), new AddressRequest("01001-000", "100", null));

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.registerUser(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("E-mail já cadastrado no sistema");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for findProfileById")
    class FindProfileByIdTests {

        @Test
        @DisplayName("Should return user profile when ID exists")
        void givenExistingId_whenFindProfileById_shouldReturnUserProfileResponse() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            UserProfileResponse response = userService.findProfileById(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.fullName()).isEqualTo("Thomaz Collet");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void givenNonExistingId_whenFindProfileById_shouldThrowResourceNotFoundException() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findProfileById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Usuário não encontrado com o ID: 99");
        }
    }

    @Nested
    @DisplayName("Tests for updatePassword")
    class UpdatePasswordTests {

        @Test
        @DisplayName("Should update password when current password matches")
        void givenValidCurrentPassword_whenUpdatePassword_shouldUpdateUserPassword() {
            UpdatePasswordRequest request = new UpdatePasswordRequest("secret123", "newSecret123");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.currentPassword(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(request.newPassword())).thenReturn("new_encoded_pass");

            userService.updatePassword(1L, request);

            verify(passwordEncoder).encode("newSecret123");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw BusinessException when current password does not match")
        void givenIncorrectCurrentPassword_whenUpdatePassword_shouldThrowBusinessException() {
            UpdatePasswordRequest request = new UpdatePasswordRequest("wrong_pass", "newSecret123");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.currentPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.updatePassword(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A senha atual informada está incorreta");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for deleteUser")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user when ID exists")
        void givenExistingId_whenDeleteUser_shouldDeleteUserFromRepository() {
            when(userRepository.existsById(1L)).thenReturn(true);

            userService.deleteUser(1L);

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existing ID")
        void givenNonExistingId_whenDeleteUser_shouldThrowResourceNotFoundException() {
            when(userRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).deleteById(any());
        }
    }
}