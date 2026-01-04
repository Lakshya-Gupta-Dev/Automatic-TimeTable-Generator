package com.group.Timetable.Generator.controller1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.AuthenticationResponse;
import com.group.Timetable.Generator.dto.LoginRequest;
import com.group.Timetable.Generator.dto.UserRegister;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController1 {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController1(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegister userRegister) throws Exception {
        AuthenticationResponse resp = userService.register(userRegister);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        AuthenticationResponse resp = userService.login(loginRequest);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @GetMapping("/users/count")
    public Long getUserCount() {
        return userRepository.count();
    }


}

