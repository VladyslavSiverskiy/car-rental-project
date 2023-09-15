package com.vsiver.spring.car_rent_project.controllers;


import com.vsiver.spring.car_rent_project.config.JwtService;
import com.vsiver.spring.car_rent_project.dtos.AuthenticationRequest;
import com.vsiver.spring.car_rent_project.dtos.AuthenticationResponse;
import com.vsiver.spring.car_rent_project.dtos.RegisterRequest;
import com.vsiver.spring.car_rent_project.exceptions.UserAlreadyExistException;
import com.vsiver.spring.car_rent_project.services.EmailService;
import com.vsiver.spring.car_rent_project.user.Role;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController

@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {

        User user = User.builder()
                .firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .pass(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(Role.ADMIN)
                .build();

        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            userRepository.save(user);
        } else {
            throw new UserAlreadyExistException("User with such email already exists.");
        }
        String successRegistrationMessage = String.format("Register user with email %s", registerRequest.getEmail());
        logger.info(successRegistrationMessage);
        emailService.sendEmail(registerRequest.getEmail(), "Registration at 'Car Rental Application'",
                "Dear " + registerRequest.getFirstname() + " , your account was successfully created!");
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().jwt(jwtToken).build());
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        String loginAttemptMessage = String.format("Attempt of login, email: %s, password: %s",
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        );
        logger.info(loginAttemptMessage);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        User user;
        Optional<User> userOptional = userRepository
                .findByEmail(authenticationRequest.getEmail());
        if(userOptional.isPresent()) {
            user = userOptional.get();
        }else {
            throw new UsernameNotFoundException("Authentication error");
        }
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().jwt(jwtToken).build());
    }
}
