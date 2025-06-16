package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.Services.UserService;
import com.springboot.cineverse_two.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser){


        User user = userService.login(loginUser.getEmail().trim(), loginUser.getPassword().trim());
        if(user != null){
            return ResponseEntity.ok(user);
        }
        else{
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
