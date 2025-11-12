package com.springboot.cineverse_two.Services;

import com.springboot.cineverse_two.Repository.UserRepository;
import com.springboot.cineverse_two.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    public enum RegistrationStatus {
        SUCCESS, EMAIL_EXISTS, FAILURE
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegistrationStatus registerNewUserServiceMethod(String fName, String email, String password, MultipartFile profileImageFile) {

        // Check for existing email
        List<User> existingUsers = userRepository.findAllByEmail(email);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            return RegistrationStatus.EMAIL_EXISTS;
        }

        try {
            User user = new User();
            user.setFullName(fName);
            user.setEmail(email);

            // Hash password
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);

            // Save profile image if provided
            String profileImageUrl = null;
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                try {
                    String fileExtension = ".jpg"; // default
                    String originalName = profileImageFile.getOriginalFilename();
                    if (originalName != null && originalName.contains(".")) {
                        fileExtension = originalName.substring(originalName.lastIndexOf("."));
                    }

                    String fileName = "profile_" + UUID.randomUUID() + fileExtension;

                    File uploadDir = new File("uploaded/profile_images");
                    uploadDir.mkdirs();

                    File outFile = new File(uploadDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(profileImageFile.getBytes());
                    }

                    // In production, build the URL dynamically from application config
                    profileImageUrl = "/profile_images/" + fileName;

                } catch (Exception e) {
                    e.printStackTrace();
                    profileImageUrl = null;
                }
            }
            user.setProfileImageUrl(profileImageUrl);

            userRepository.save(user);
            return RegistrationStatus.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return RegistrationStatus.FAILURE;
        }
    }

    public User login(String email, String password) {
        List<User> users = userRepository.findAllByEmail(email);
        if (users.size() > 1) {
            throw new IllegalStateException("Multiple users found with the same email: " + email);
        }
        if (users.size() == 1) {
            User user = users.get(0);
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        List<User> users = userRepository.findAllByEmail(email);
        if (users.size() != 1) {
            return false;
        }

        User user = users.get(0);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean updateUserProfile(String currentEmail, String newFullName, String newEmail, String newProfileImageUrl) {
        List<User> users = userRepository.findAllByEmail(currentEmail);
        if (users.size() != 1) {
            return false;
        }

        if (!newEmail.equalsIgnoreCase(currentEmail) && !userRepository.findAllByEmail(newEmail).isEmpty()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = users.get(0);
        user.setFullName(newFullName);
        user.setEmail(newEmail);

        if (newProfileImageUrl != null && !newProfileImageUrl.isEmpty()) {
            user.setProfileImageUrl(newProfileImageUrl);
        }

        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        List<User> users = userRepository.findAllByEmail(email);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }
}
