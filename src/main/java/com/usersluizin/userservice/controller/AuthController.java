package com.usersluizin.userservice.controller;

import com.usersluizin.userservice.dto.AuthResponse;
import com.usersluizin.userservice.dto.LoginRequest;
import com.usersluizin.userservice.mapper.UserMapper;
import com.usersluizin.userservice.security.ApplicationUserDetails;
import com.usersluizin.userservice.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, UserMapper.toResponse(userDetails.getUser())));
    }
}
