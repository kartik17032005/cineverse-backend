package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.Services.UserService;
import com.springboot.cineverse_two.Services.UserService.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class RegisterUserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerNewUser(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        // Basic validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing fields", HttpStatus.BAD_REQUEST);
        }

        // Optional: Email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        // Debug log without password
        System.out.println("Received registration request for: " + fullName + " / " + email +
                " | Profile Image: " + (profileImage != null ? "[Provided]" : "[Not Provided]"));

        RegistrationStatus status = userService.registerNewUserServiceMethod(fullName, email, password, profileImage);

        switch (status) {
            case EMAIL_EXISTS:
                return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
            case SUCCESS:
                return new ResponseEntity<>("success", HttpStatus.OK);
            default:
                return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
