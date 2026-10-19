package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.AuthenticationResult;
import com.mrodriguezul.apptapp.domain.service.UserSecurityService;
import com.mrodriguezul.apptapp.web.dto.LoginRequestDto;
import com.mrodriguezul.apptapp.web.dto.LoginResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Public authentication operations")
public class AuthController {

    private final UserSecurityService userSecurityService;

    public AuthController(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user",
            description = "Authenticates credentials and returns a JWT Bearer token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoginRequestDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or incomplete credentials"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        AuthenticationResult authResult = userSecurityService.loginUser(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponseDto(authResult.token(), "Bearer", authResult.expiresIn()));
    }
}
