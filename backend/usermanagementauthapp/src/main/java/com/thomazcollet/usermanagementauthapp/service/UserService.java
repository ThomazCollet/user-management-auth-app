package com.thomazcollet.usermanagementauthapp.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.thomazcollet.usermanagementauthapp.dto.response.AddressResponse;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
import com.thomazcollet.usermanagementauthapp.infra.feign.dto.ViaCepResponse;
import com.thomazcollet.usermanagementauthapp.repository.RoleRepository;
import com.thomazcollet.usermanagementauthapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ViaCepService viaCepService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileResponse registerUser(RegisterUserRequest request) {
        validateUniqueFields(request);

        // Busca endereço via ViaCEP e monta a entidade Address
        ViaCepResponse viaCep = viaCepService.findAddressByZipCode(request.address().zipCode());
        Address address = buildAddress(request, viaCep);

        // Busca a role padrao ROLE_USER
        Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role padrão ROLE_USER não encontrada"));

        // Cria a entidade User
        User user = User.builder()
                .fullName(request.fullName())
                .cpf(request.cpf())
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .birthDate(request.birthDate())
                .isEmailVerified(false)
                .address(address)
                .roles(Set.of(defaultRole))
                .build();

        User savedUser = userRepository.save(user);

        return mapToUserProfileResponse(savedUser);
    }

    private void validateUniqueFields(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado no sistema");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Nome de usuário já cadastrado no sistema");
        }
        if (userRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF já cadastrado no sistema");
        }
    }

    private Address buildAddress(RegisterUserRequest request, ViaCepResponse viaCep) {
        return Address.builder()
                .zipCode(viaCep.cep())
                .street(viaCep.logradouro())
                .number(request.address().number())
                .complement(request.address().complement())
                .neighborhood(viaCep.bairro())
                .city(viaCep.localidade())
                .state(viaCep.uf())
                .build();
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        AddressResponse addressResp = null;
        if (user.getAddress() != null) {
            addressResp = new AddressResponse(
                    user.getAddress().getId(),
                    user.getAddress().getZipCode(),
                    user.getAddress().getStreet(),
                    user.getAddress().getNumber(),
                    user.getAddress().getComplement(),
                    user.getAddress().getNeighborhood(),
                    user.getAddress().getCity(),
                    user.getAddress().getState());
        }

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toSet());

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getCpf(),
                user.getEmail(),
                user.getUsername(),
                user.getPhone(),
                user.getBirthDate(),
                user.getIsEmailVerified(),
                addressResp,
                roles);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return mapToUserProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        // Substitui os 3 setters por um único método expressivo de domínio:
        user.updateProfile(request.fullName(), request.phone(), request.birthDate());

        User updatedUser = userRepository.save(user);
        return mapToUserProfileResponse(updatedUser);
    }

    @Transactional
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("A senha atual informada está incorreta");
        }

        // Usa o método semântico já existente na entidade User:
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserProfileResponse updateAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));

        ViaCepResponse viaCep = viaCepService.findAddressByZipCode(request.zipCode());

        Address address = user.getAddress();
        if (address == null) {
            address = Address.builder()
                    .zipCode(viaCep.cep())
                    .street(viaCep.logradouro())
                    .number(request.number())
                    .complement(request.complement())
                    .neighborhood(viaCep.bairro())
                    .city(viaCep.localidade())
                    .state(viaCep.uf())
                    .build();
            user.updateAddress(address);
        } else {
            // Atualização limpa via método de domínio da entidade Address:
            address.updateDetails(
                    viaCep.cep(),
                    viaCep.logradouro(),
                    request.number(),
                    request.complement(),
                    viaCep.bairro(),
                    viaCep.localidade(),
                    viaCep.uf());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserProfileResponse(updatedUser);
    }
}