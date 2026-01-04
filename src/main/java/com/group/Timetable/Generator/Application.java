
package com.group.Timetable.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.entities.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    public static void main(String[] args) {
        System.out.println("Time Table Generator ..............");
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void initAdmin() {
        // Check if admin already exists
        if (!userRepository.existsByUsername("admin")) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setEmail("admin@timetable.com");
            admin.setMobile(9999999999L);
            admin.setInstituteName("System");

            userRepository.save(admin);

            System.out.println("Admin auto-created at startup");
        } else {
            System.out.println("Admin already exists — skipping creation");
        }
    }
}
