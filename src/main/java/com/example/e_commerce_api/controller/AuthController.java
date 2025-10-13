package com.example.e_commerce_api.controller;


import com.example.e_commerce_api.dto.AuthDTO;
import com.example.e_commerce_api.dto.RegisterDTO;
import com.example.e_commerce_api.model.UserRole;
import com.example.e_commerce_api.model.Users;
import com.example.e_commerce_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO authDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                authDTO.email(),
                authDTO.password()
        );
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO registerDTO){
        if(this.userRepository.findByEmail(registerDTO.email()) != null ){
            return ResponseEntity.badRequest().body("Email already in use");
        }

        String encodedPassword = passwordEncoder.encode(registerDTO.password());

        Users user = new Users(
                registerDTO.email(),
                encodedPassword,
                registerDTO.Role()
        );
            this.userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
