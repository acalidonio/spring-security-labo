package com.server.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.app.dto.auth.AuthResponseDto;
import com.server.app.dto.auth.LoginRequestDto;
import com.server.app.dto.auth.SignupRequestDto;
import com.server.app.dto.auth.UpdatePasswordRequestDto;
import com.server.app.dto.auth.UpdateProfileRequestDto;
import com.server.app.entities.User;
import com.server.app.services.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@Valid @RequestBody SignupRequestDto dto) {
        return ResponseEntity.ok(authService.signup(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getProfile(user.getId()));
    }

    @PutMapping("/update/profile")
    public ResponseEntity<AuthResponseDto> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequestDto dto) {
        return ResponseEntity.ok(authService.updateProfile(user.getId(), dto));
    }

    @PutMapping("/update/password")
    public ResponseEntity<User> updatePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdatePasswordRequestDto dto) {
        return ResponseEntity.ok(authService.updatePassword(user.getId(), dto));
    }
}
