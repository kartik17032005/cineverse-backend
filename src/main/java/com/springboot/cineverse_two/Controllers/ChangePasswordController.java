package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.Services.UserService;
import com.springboot.cineverse_two.DTO.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean result = userService.changePassword(
                request.getEmail(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("success", true);
            response.put("message", "Password changed successfully!");
            return ResponseEntity.ok(response);
        }
        response.put("success", true);
        response.put("message", "Old password incorrect or user not found.");
        return ResponseEntity.badRequest().body(response);}
}

