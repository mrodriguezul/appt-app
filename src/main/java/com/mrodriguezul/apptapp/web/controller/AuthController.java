package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.service.UserSecurityService;
import com.mrodriguezul.apptapp.web.dto.LoginRequestDto;
import com.mrodriguezul.apptapp.web.dto.LoginResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserSecurityService userSecurityService;

    public AuthController(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        String token = userSecurityService.loginUser(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponseDto(token, "Bearer", 3600));
    }
}
