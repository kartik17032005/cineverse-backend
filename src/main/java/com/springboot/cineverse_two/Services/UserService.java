package com.springboot.cineverse_two.Services;

import com.springboot.cineverse_two.Repository.UserRepository;
import com.springboot.cineverse_two.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public int registerNewUserServiceMethod(String fName, String email, String password) {
        // Check if user already exists by email
        List<User> existingUsers = userRepository.findByEmail(email);
        if (!existingUsers.isEmpty()) {
            return -1; // Email already exists
        }

        try {
            // Create new User object
            User user = new User();
            user.setFullName(fName);
            user.setEmail(email);
            user.setPassword(password); // Already hashed before calling this method

            // Save to database
            userRepository.save(user);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // General failure
        }
    }

    public User login(String email, String password){
        List<User> users = userRepository.findByEmail(email);
        for(User user: users){
            System.out.println("Stored Hash: " + user.getPassword());
            if(BCrypt.checkpw(password, user.getPassword())){
                return user;
            }
        }
        return null;
    }
}
