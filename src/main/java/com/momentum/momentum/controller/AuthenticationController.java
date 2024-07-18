package com.momentum.momentum.controller;

import com.momentum.momentum.entity.User;
import com.momentum.momentum.exception.UserIdAlreadyExistsException;
import com.momentum.momentum.model.AuthenticationRequest;
import com.momentum.momentum.model.AuthenticationResponse;
import com.momentum.momentum.model.RegisterResponse;
import com.momentum.momentum.service.JwtUtil;
import com.momentum.momentum.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final MyUserDetailsService myUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("[Auth] POST /auth/login");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserId(), authenticationRequest.getPassword())
            );
            System.out.printf("UserId: %s, Password: %s\n", authenticationRequest.getUserId(), authenticationRequest.getPassword());

        } catch (Exception e) {
            System.out.println("Incorrect userId or password");
            return ResponseEntity.status(401).body("Incorrect userId or password");
        }

        return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(authenticationRequest.getUserId())));
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("[Auth] POST /auth/register");
        User user = new User();
        user.setUserId(authenticationRequest.getUserId());
        user.setUsername(authenticationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));

        final String newAccessToken = jwtUtil.generateToken(user.getUserId());
        user.setRefreshToken(jwtUtil.generateRefreshToken(user.getUserId()));

        myUserDetailsService.saveUser(user);
        RegisterResponse response = new RegisterResponse(user, newAccessToken);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserIdAlreadyExistsException.class)
    public ResponseEntity<String> handleUserIdAlreadyExistsException(UserIdAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("[Auth] POST /auth/refreshToken");
        System.out.println("refreshToken: " + authenticationRequest.getRefreshToken());

        try {
            final String refreshToken = authenticationRequest.getRefreshToken();

            User user = myUserDetailsService.findByUserId(jwtUtil.extractUserId(refreshToken));

            if (jwtUtil.validateRefreshToken(refreshToken) &&  refreshToken.equals(user.getRefreshToken())) {
                final String newAccessToken = jwtUtil.generateToken(user.getUserId());
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken));
            } else {
                return ResponseEntity.status(401).body("Invalid refreshToken Or refreshToken expired");
            }
        } catch (Exception e) {
            System.out.println("Token refresh failed");
            return ResponseEntity.status(500).body("Token refresh failed");
        }
    }
}
