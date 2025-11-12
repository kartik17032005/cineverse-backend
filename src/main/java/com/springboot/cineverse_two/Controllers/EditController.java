package com.springboot.cineverse_two.Controllers;

import com.springboot.cineverse_two.DTO.UserProfileUpdateRequest;
import com.springboot.cineverse_two.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EditController {

    @Autowired
    private UserService userService;

    @PutMapping("/edit-profile")
    public ResponseEntity<?> editUserProfile(@RequestBody UserProfileUpdateRequest request) {
        boolean isUpdated = userService.updateUserProfile(
                request.getCurrentEmail(),
                request.getNewFullName(),
                request.getNewEmail(),
                request.getNewProfileImageUrl()
        );

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found or update failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
