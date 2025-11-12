package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.Services.UserService;
import com.springboot.cineverse_two.DTO.LoginRequest;
import com.springboot.cineverse_two.DTO.UserDTO;
import com.springboot.cineverse_two.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Validate input
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
                loginRequest.getEmail().trim().isEmpty() || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email and password must not be empty");
        }

        // Optional: format validation
        if (!loginRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        System.out.println("Login attempt for: " + loginRequest.getEmail());

        User user = userService.login(loginRequest.getEmail().trim(), loginRequest.getPassword().trim());
        if (user != null) {
            // Return a safe DTO with profile image URL
            return ResponseEntity.ok(new UserDTO(user));
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
