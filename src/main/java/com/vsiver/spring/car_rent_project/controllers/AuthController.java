package com.vsiver.spring.car_rent_project.controllers;


import com.vsiver.spring.car_rent_project.config.JwtService;
import com.vsiver.spring.car_rent_project.dtos.AuthenticationRequest;
import com.vsiver.spring.car_rent_project.dtos.AuthenticationResponse;
import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import com.vsiver.spring.car_rent_project.dtos.RegisterRequest;
import com.vsiver.spring.car_rent_project.exceptions.NoCarWithSuchIdException;
import com.vsiver.spring.car_rent_project.user.Role;
import com.vsiver.spring.car_rent_project.user.User;
import com.vsiver.spring.car_rent_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@RestController

@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) throws NoCarWithSuchIdException {

        User user = User.builder()
                .firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .pass(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(Role.ADMIN)
                .build();
        System.out.println(user);
        if (!userRepository.findByEmail(user.getEmail()).isPresent()) {
            userRepository.save(user);
        } else {
            throw new NoCarWithSuchIdException("such user exists");
        }

        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().jwt(jwtToken).build());
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println(authenticationRequest.getEmail());
        System.out.println(authenticationRequest.getPassword());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        User user = userRepository
                .findByEmail(authenticationRequest.getEmail())
                .get();
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().jwt(jwtToken).build());
    }
}
