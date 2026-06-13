package com.server.app.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.AuthResponseDto;
import com.server.app.dto.auth.LoginRequestDto;
import com.server.app.dto.auth.SignupRequestDto;
import com.server.app.dto.auth.UpdatePasswordRequestDto;
import com.server.app.dto.auth.UpdateProfileRequestDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.exceptions.UnauthorizedException;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebToken jwtUtil;

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (user.isBlocked()) {
            throw new UnauthorizedException("Your account has been blocked");
        }

        if (user.getRole() == null || Boolean.FALSE.equals(user.getRole().getActive())) {
            throw new UnauthorizedException("Your account role is not active");
        }

        String token = jwtUtil.createToken(user);
        return new AuthResponseDto(token, user);
    }

    @Transactional
    public AuthResponseDto signup(SignupRequestDto dto) {
        if (userRepository.findUserByUsername(dto.getUsername()).isPresent()) {
            throw new ConfictException("El nombre de usuario ya está en uso");
        }

        if (userRepository.findUserByEmail(dto.getEmail()).isPresent()) {
            throw new ConfictException("El correo electrónico ya está en uso");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new NotFoundException("Rol ADMIN no encontrado"));

        if (Boolean.FALSE.equals(adminRole.getActive())) {
            throw new UnauthorizedException("El rol ADMIN no está activo");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(adminRole);
        user.setBlocked(false);

        user = userRepository.save(user);
        String token = jwtUtil.createToken(user);

        return new AuthResponseDto(token, user);
    }

    public User getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
                
        if (user.isBlocked()) {
            throw new UnauthorizedException("Your account has been blocked");
        }
        
        return user;
    }

    @Transactional
    public AuthResponseDto updateProfile(Integer userId, UpdateProfileRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (user.isBlocked()) {
            throw new UnauthorizedException("Your account has been blocked");
        }

        userRepository.findUserByUsername(dto.getUsername()).ifPresent(existing -> {
            if (existing.getId() != userId) {
                throw new ConfictException("El nombre de usuario ya está en uso");
            }
        });

        userRepository.findUserByEmail(dto.getEmail()).ifPresent(existing -> {
            if (existing.getId() != userId) {
                throw new ConfictException("El correo electrónico ya está en uso");
            }
        });

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());

        user = userRepository.save(user);
        String token = jwtUtil.createToken(user);

        return new AuthResponseDto(token, user);
    }

    @Transactional
    public User updatePassword(Integer userId, UpdatePasswordRequestDto dto) {
        if (!dto.getNewpassword().equals(dto.getConfirmpassword())) {
            throw new BadRequestException("La nueva contraseña y la confirmación no coinciden");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getOldpassword(), user.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewpassword()));
        return userRepository.save(user);
    }
}
