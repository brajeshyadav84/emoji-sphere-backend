package com.emojisphere.repository;

import com.emojisphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByMobileNumber(String mobileNumber);

    Optional<User> findById(Long id);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByMobileNumber(String mobileNumber);
    
    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.mobileNumber = :mobile OR u.email = :email")
    Optional<User> findByMobileOrEmail(@Param("mobile") String mobile, @Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();

}