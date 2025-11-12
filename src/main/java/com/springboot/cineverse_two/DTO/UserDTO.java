package com.springboot.cineverse_two.DTO;

import com.springboot.cineverse_two.models.User;

public class UserDTO {
    private int userId;
    private String fullName;
    private String email;
    private String profileImageUrl;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.profileImageUrl = user.getProfileImageUrl();
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
