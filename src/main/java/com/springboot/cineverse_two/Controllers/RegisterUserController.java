package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterUserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestParam("fullName") String fullName,
                                                  @RequestParam("email") String email,
                                                  @RequestParam("password") String password) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("missing fields", HttpStatus.BAD_REQUEST);
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        int result = userService.registerNewUserServiceMethod(fullName, email, hashedPassword);

        if (result == -1) {
            return new ResponseEntity<>("email already exists", HttpStatus.CONFLICT);
        } else if (result != 1) {
            return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
