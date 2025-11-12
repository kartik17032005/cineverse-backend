// UserProfileUpdateRequest.java
package com.springboot.cineverse_two.DTO;

public class UserProfileUpdateRequest {
    private String currentEmail;
    private String newFullName;
    private String newEmail;
    private String newProfileImageUrl;

    // Getters and Setters
    public String getCurrentEmail() {
        return currentEmail;
    }
    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }
    public String getNewFullName() {
        return newFullName;
    }
    public void setNewFullName(String newFullName) {
        this.newFullName = newFullName;
    }
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
    public String getNewProfileImageUrl() {
        return newProfileImageUrl;
    }
    public void setNewProfileImageUrl(String newProfileImageUrl) {
        this.newProfileImageUrl = newProfileImageUrl;
    }
}
