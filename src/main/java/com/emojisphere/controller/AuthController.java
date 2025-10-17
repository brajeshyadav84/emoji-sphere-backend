package com.emojisphere.controller;

import com.emojisphere.dto.*;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.OtpService;
import com.emojisphere.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    OtpService otpService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpiration;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest otpRequest) {
        try {
            String otp = otpService.generateAndSaveOtp(otpRequest.getMobile());
            otpService.sendOtp(otpRequest.getMobile(), otp);
            
            return ResponseEntity.ok(new MessageResponse("OTP sent successfully to " + otpRequest.getMobile()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to send OTP. " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyRequest otpVerifyRequest) {
        boolean isValid = otpService.verifyOtp(otpVerifyRequest.getMobile(), otpVerifyRequest.getOtp());
        
        if (isValid) {
            return ResponseEntity.ok(new ValidationResponse(true, "OTP verified successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ValidationResponse(false, "Invalid or expired OTP"));
        }
    }

    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        try {
            // Check if email already exists and is verified
            Optional<User> existingUser = userRepository.findByEmail(emailOtpRequest.getEmail());
            if (existingUser.isPresent() && existingUser.get().getIsVerified()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: This email is already verified!"));
            }
            
            String otp = otpService.generateAndSaveEmailOtp(emailOtpRequest.getEmail());
            otpService.sendEmailOtp(emailOtpRequest.getEmail(), otp);
            
            return ResponseEntity.ok(new MessageResponse("OTP sent successfully to " + emailOtpRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to send email OTP. " + e.getMessage()));
        }
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest emailOtpVerifyRequest) {
        try {
            boolean isValid = otpService.verifyEmailOtp(emailOtpVerifyRequest.getEmail(), emailOtpVerifyRequest.getOtp());
            
            if (isValid) {
                // Update user verification status
                Optional<User> userOpt = userRepository.findByEmail(emailOtpVerifyRequest.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setIsVerified(true);
                    userRepository.save(user);
                    
                    return ResponseEntity.ok(new ValidationResponse(true, "Email verified successfully! Your account is now active."));
                } else {
                    return ResponseEntity.badRequest()
                            .body(new ValidationResponse(false, "User not found with this email address"));
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(new ValidationResponse(false, "Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ValidationResponse(false, "Error verifying OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/validate-mobile")
    public ResponseEntity<?> validateMobile(@Valid @RequestBody OtpRequest otpRequest) {
        // Check if mobile number is already registered
        if (userRepository.existsByMobileNumber(otpRequest.getMobile())) {
            return ResponseEntity.badRequest()
                    .body(new ValidationResponse(false, "Mobile number is already registered!"));
        }
        
        return ResponseEntity.ok(new ValidationResponse(true, "Mobile number is available"));
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody String email) {
        if (email != null && !email.trim().isEmpty() && userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(new ValidationResponse(false, "Email is already in use!"));
        }
        
        return ResponseEntity.ok(new ValidationResponse(true, "Email is available"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    // Ensure user exists and is verified before attempting authentication
    Optional<User> userOpt = userRepository.findByMobileNumber(loginRequest.getMobile());
    if (userOpt.isEmpty()) {
        return ResponseEntity.ok()
            .body(new MessageResponse("Error: No account found with this mobile number."));
    }

    User user = userOpt.get();
    if (user.getIsVerified() == null || !user.getIsVerified()) {
        return ResponseEntity.ok()
            .body(new MessageResponse( "Error: Account verification is pending. Please verify your account before signing in.", user.getEmail()));
    }

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getMobile(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = generateJwtToken(authentication);

    UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getFullName(),
        userDetails.getMobile(),
        userDetails.getEmail(),
        userDetails.getRole())); // Use actual user role
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Validate password confirmation
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Password and confirm password do not match!"));
        }

        if (userRepository.existsByMobileNumber(signUpRequest.getMobile())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Mobile number is already registered!"));
        }

        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Email is required for registration
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is required for registration!"));
        }

        // Create new user's account with new constructor
        User user = new User(
            signUpRequest.getMobile(),
            signUpRequest.getFullName(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getDob(),
            signUpRequest.getCountry(),
            signUpRequest.getGender(),
            signUpRequest.getSchoolName()
        );

        user.setEmail(signUpRequest.getEmail());
        user.setIsVerified(false); // Will be verified after OTP verification
        
        // Set role based on request, default to USER if not specified
        String userRole = "USER"; // Default role
        if (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) {
            // Take the first role from the set
            userRole = signUpRequest.getRole().iterator().next();
            // Validate role - only allow USER, TEACHER, and ADMIN
            if (!userRole.equals("USER") && !userRole.equals("TEACHER") && !userRole.equals("ADMIN")) {
                userRole = "USER"; // Fallback to USER for invalid roles
            }
        }
        user.setRole(userRole);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        // Check if user exists by email
        if (!userRepository.existsByEmail(emailOtpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: No account found with this email address!"));
        }

        try {
            String otp = otpService.generateAndSaveEmailOtp(emailOtpRequest.getEmail());
            otpService.sendEmailOtp(emailOtpRequest.getEmail(), otp);
            
            return ResponseEntity.ok(new MessageResponse("Password reset OTP sent successfully to " + emailOtpRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to send password reset OTP. " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody EmailResetPasswordRequest resetPasswordRequest) {
        // Verify OTP first
        if (!otpService.verifyEmailOtp(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid or expired OTP"));
        }

        // Validate password confirmation
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Password and confirm password do not match!"));
        }

        // Find user by email and update password
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(encoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

    private String generateJwtToken(Authentication authentication) {
        UserDetailsServiceImpl.UserPrincipal userPrincipal = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

        SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .setSubject((userPrincipal.getMobile())) // Use mobile as subject
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}