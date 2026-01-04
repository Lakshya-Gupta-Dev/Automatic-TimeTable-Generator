package com.group.Timetable.Generator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.AuthenticationResponse;
import com.group.Timetable.Generator.dto.LoginRequest;
import com.group.Timetable.Generator.dto.UserRegister;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.security.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------------- REGISTER USER --------------------------
    public AuthenticationResponse register(UserRegister userRegister) throws Exception {

        if (userRepository.existsByUsername(userRegister.getUsername())) {
            throw new Exception("Username already taken");
        }
        if (userRepository.existsByEmail(userRegister.getEmail())) {
            throw new Exception("Email already registered");
        }
        if (userRegister.getMobile() != null && userRepository.existsByMobile(userRegister.getMobile())) {
            throw new Exception("Mobile already registered");
        }
        if (userRegister.getPassword() == null || userRegister.getConfirmPassword() == null
                || !userRegister.getPassword().equals(userRegister.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        User user = new User();
        user.setUsername(userRegister.getUsername());
        user.setInstituteName(userRegister.getInstituteName());
        user.setMobile(userRegister.getMobile());
        user.setEmail(userRegister.getEmail());
        user.setPassword(passwordEncoder.encode(userRegister.getPassword()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthenticationResponse(token);
    }

    // ---------------------- LOGIN USER / ADMIN --------------------------
    public AuthenticationResponse login(LoginRequest loginRequest) throws Exception {

        // Authenticate user (ADMIN + USERS both)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new Exception("Invalid username or password");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        return new AuthenticationResponse(token);
    }
}
