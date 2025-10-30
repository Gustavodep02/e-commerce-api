package com.example.e_commerce_api.controller;


import com.example.e_commerce_api.dto.AuthDTO;
import com.example.e_commerce_api.dto.LoginResponseDTO;
import com.example.e_commerce_api.dto.RegisterDTO;
import com.example.e_commerce_api.infra.security.TokenService;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name= "authentication", description = "Endpoints for user authentication and registration")
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;


    @PostMapping("/login")
    @Operation(summary = "Returns a JWT token upon successful authentication", description = "Checks user credentials and returns a JWT token if valid")
    @ApiResponse(responseCode = "200", description = "Successful authentication")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthDTO authDTO){
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(
                    authDTO.email(),
                    authDTO.password()
            );
            var authentication = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) authentication.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registers a new user", description = "Creates a new user account with the provided details")
    @ApiResponse(responseCode = "200", description = "Successful registration")
    @ApiResponse(responseCode = "400", description = "Email already in use")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO){
        if(this.userRepository.findByEmail(registerDTO.email()) != null ){
            return ResponseEntity.badRequest().body("Email already in use");
        }

        String encodedPassword = passwordEncoder.encode(registerDTO.password());

        User user = new User(
                registerDTO.email(),
                encodedPassword,
                registerDTO.name()
        );
        this.userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
