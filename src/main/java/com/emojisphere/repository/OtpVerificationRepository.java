package com.emojisphere.repository;

import com.emojisphere.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    
    Optional<OtpVerification> findByMobileAndOtpAndVerifiedFalseAndExpiresAtAfter(
            String mobile, String otp, LocalDateTime currentTime);
    
    // Email OTP methods
    Optional<OtpVerification> findByEmailAndOtpAndVerifiedFalseAndExpiresAtAfter(
            String email, String otp, LocalDateTime currentTime);
    
    @Query("SELECT o FROM OtpVerification o WHERE o.mobile = :mobile AND o.verified = false AND o.expiresAt > :currentTime ORDER BY o.createdAt DESC")
    Optional<OtpVerification> findLatestUnverifiedOtpByMobile(@Param("mobile") String mobile, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT o FROM OtpVerification o WHERE o.email = :email AND o.verified = false AND o.expiresAt > :currentTime ORDER BY o.createdAt DESC")
    Optional<OtpVerification> findLatestUnverifiedOtpByEmail(@Param("email") String email, @Param("currentTime") LocalDateTime currentTime);
    
    void deleteByMobileAndExpiresAtBefore(String mobile, LocalDateTime currentTime);
    
    void deleteByEmailAndExpiresAtBefore(String email, LocalDateTime currentTime);
    
    void deleteByExpiresAtBefore(LocalDateTime currentTime);
}