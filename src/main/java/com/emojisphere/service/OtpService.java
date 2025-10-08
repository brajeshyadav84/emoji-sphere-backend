package com.emojisphere.service;

import com.emojisphere.entity.OtpVerification;
import com.emojisphere.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    private final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    public String generateAndSaveOtp(String mobile) {
        // Generate 6-digit OTP
        String otp = generateOtp();
        
        // Calculate expiry time (5 minutes from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Clean up old OTPs for this mobile
        cleanupExpiredOtps(mobile);
        
        // Save new OTP
        OtpVerification otpVerification = new OtpVerification(mobile, otp, expiryTime);
        otpRepository.save(otpVerification);
        
        return otp;
    }

    public boolean verifyOtp(String mobile, String otp) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        Optional<OtpVerification> otpVerificationOpt = otpRepository
                .findByMobileAndOtpAndVerifiedFalseAndExpiresAtAfter(mobile, otp, currentTime);
        
        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            otpVerification.setVerified(true);
            otpRepository.save(otpVerification);
            return true;
        }
        
        return false;
    }

    public boolean isOtpVerified(String mobile) {
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<OtpVerification> latestOtp = otpRepository
                .findLatestUnverifiedOtpByMobile(mobile, currentTime);
        
        return latestOtp.isEmpty(); // If no unverified OTP found, it means the latest one was verified
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void cleanupExpiredOtps(String mobile) {
        LocalDateTime currentTime = LocalDateTime.now();
        otpRepository.deleteByMobileAndExpiresAtBefore(mobile, currentTime);
    }

    @Transactional
    public void cleanupAllExpiredOtps() {
        LocalDateTime currentTime = LocalDateTime.now();
        otpRepository.deleteByExpiresAtBefore(currentTime);
    }

    // For testing/demo purposes - in production, you would integrate with SMS service
    public void sendOtp(String mobile, String otp) {
        // TODO: Integrate with SMS service provider (Twilio, AWS SNS, etc.)
        System.out.println("Sending OTP " + otp + " to mobile number: " + mobile);
        
        // For demo purposes, you could store in a temporary location or just log
        // In production, replace this with actual SMS sending logic
    }
}