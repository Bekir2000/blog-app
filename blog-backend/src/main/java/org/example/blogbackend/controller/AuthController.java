package org.example.blogbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.mapper.UserMapper;
import org.example.blogbackend.model.dto.requests.LoginRequest;
import org.example.blogbackend.model.dto.responses.AuthResponse;
import org.example.blogbackend.model.dto.responses.UserResponse;
import org.example.blogbackend.model.entities.User;
import org.example.blogbackend.security.BlogUserDetails;
import org.example.blogbackend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(60 * 60 * 24) // 24h
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal BlogUserDetails principal) {
        User user = principal.getUser();
        UserResponse dto = userMapper.toDto(user);
        return ResponseEntity.ok(dto);
    }



}
