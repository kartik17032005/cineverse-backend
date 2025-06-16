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

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO cineverse(full_name, email, password) VALUES(:fullName, :email, :password)", nativeQuery = true)
    public int registerNewUser(@Param("fullName") String fullName,
                               @Param("email") String email,
                               @Param("password") String password);

    public List<User> findByEmail(String email);
}
