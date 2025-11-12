package com.springboot.cineverse_two.Repository;

import com.springboot.cineverse_two.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    // Register a new user using a native query
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO cineverse(full_name, email, password, profile_image_url) VALUES(:fullName, :email, :password, :profileImageUrl)", nativeQuery = true)
    int registerNewUser(@Param("fullName") String fullName,
                        @Param("email") String email,
                        @Param("password") String password,
                        @Param("profileImageUrl") String profileImageUrl);

    // For debugging duplicates: get all users by email
    @Query("SELECT u FROM User u WHERE u.email = :email")
    List<User> findAllByEmail(@Param("email") String email);

    // Temporary replacement to avoid NonUniqueResultException
    // Change back to Optional<User> after cleaning duplicates and DB constraint
    default User findByEmailSafe(String email) {
        List<User> users = findAllByEmail(email);
        if (users.size() > 1) {
            throw new IllegalStateException("Multiple users found with the same email: " + email);
        }
        return users.isEmpty() ? null : users.get(0);
    }

    // Update user's password by email
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String newPassword);

    // Update profile info by email
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.fullName = :fullName, u.email = :newEmail, u.profileImageUrl = :profileImageUrl WHERE u.email = :currentEmail")
    int updateUserProfileByEmail(@Param("currentEmail") String currentEmail,
                                 @Param("fullName") String fullName,
                                 @Param("newEmail") String newEmail,
                                 @Param("profileImageUrl") String profileImageUrl);
}
