package com.thomazcollet.usermanagementauthapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomazcollet.usermanagementauthapp.dto.request.AddressRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.UpdatePasswordRequest;
import com.thomazcollet.usermanagementauthapp.dto.request.UpdateUserRequest;
import com.thomazcollet.usermanagementauthapp.dto.response.UserProfileResponse;
import com.thomazcollet.usermanagementauthapp.security.UserDetailsImpl;
import com.thomazcollet.usermanagementauthapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        UserProfileResponse response = userService.findProfileById(principal.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileResponse response = userService.updateProfile(principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(principal.getUser().getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/address")
    public ResponseEntity<UserProfileResponse> updateMyAddress(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody AddressRequest request) {
        UserProfileResponse response = userService.updateAddress(principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal UserDetailsImpl principal) {
        userService.deleteUser(principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}