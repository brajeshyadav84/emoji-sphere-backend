package com.emojisphere.service;

import com.emojisphere.entity.OtpVerification;
import com.emojisphere.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

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

    // Email OTP methods
    public String generateAndSaveEmailOtp(String email) {
        // Generate 6-digit OTP
        String otp = generateOtp();
        
        // Calculate expiry time (5 minutes from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Clean up old OTPs for this email
        cleanupExpiredEmailOtps(email);
        
        // Save new OTP
        OtpVerification otpVerification = new OtpVerification(email, otp, expiryTime, true);
        otpRepository.save(otpVerification);
        
        return otp;
    }

    public boolean verifyEmailOtp(String email, String otp) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        Optional<OtpVerification> otpVerificationOpt = otpRepository
                .findByEmailAndOtpAndVerifiedFalseAndExpiresAtAfter(email, otp, currentTime);
        
        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            otpVerification.setVerified(true);
            otpRepository.save(otpVerification);
            return true;
        }
        
        return false;
    }

    public boolean isEmailOtpVerified(String email) {
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<OtpVerification> latestOtp = otpRepository
                .findLatestUnverifiedOtpByEmail(email, currentTime);
        
        return latestOtp.isEmpty(); // If no unverified OTP found, it means the latest one was verified
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

    private void cleanupExpiredEmailOtps(String email) {
        LocalDateTime currentTime = LocalDateTime.now();
        otpRepository.deleteByEmailAndExpiresAtBefore(email, currentTime);
    }

    @Transactional
    public void cleanupAllExpiredOtps() {
        LocalDateTime currentTime = LocalDateTime.now();
        otpRepository.deleteByExpiresAtBefore(currentTime);
    }

    // Send email OTP
    public void sendEmailOtp(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("🔐 Your KidsSpace Verification Code");
            
            String emailBody = String.format(
                "Hi there! 👋\n\n" +
                "Your KidsSpace verification code is:\n\n" +
                "🔐 Verification Code: %s\n\n" +
                "This code will expire in 5 minutes. Please use it to verify your email address.\n\n" +
                "If you didn't request this code, please ignore this email.\n\n" +
                "Best regards,\n" +
                "The KidsSpace Team 🚀",
                otp
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
            System.out.println("Email OTP sent successfully to: " + email);
            
        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.err.println("Email authentication failed. Please check your email credentials: " + e.getMessage());
            throw new RuntimeException("Email authentication failed. Please check email configuration.", e);
        } catch (org.springframework.mail.MailException e) {
            System.err.println("Mail service error: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (Exception e) {
            System.err.println("Unexpected error sending email OTP: " + e.getMessage());
            throw new RuntimeException("Failed to send email OTP", e);
        }
    }

    // For testing/demo purposes - in production, you would integrate with SMS service
    public void sendOtp(String mobile, String otp) {
        // TODO: Integrate with SMS service provider (Twilio, AWS SNS, etc.)
        System.out.println("Sending OTP " + otp + " to mobile number: " + mobile);
        
        // For demo purposes, you could store in a temporary location or just log
        // In production, replace this with actual SMS sending logic
    }
}