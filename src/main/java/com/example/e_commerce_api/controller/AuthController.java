package com.example.e_commerce_api.controller;


import com.example.e_commerce_api.dto.AuthDTO;
import com.example.e_commerce_api.dto.LoginResponseDTO;
import com.example.e_commerce_api.dto.RegisterDTO;
import com.example.e_commerce_api.infra.security.TokenService;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthDTO authDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                authDTO.email(),
                authDTO.password()
        );
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User)authentication.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
    @PostMapping("/register")
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
