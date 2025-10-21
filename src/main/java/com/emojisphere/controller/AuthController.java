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
    public ResponseEntity<ApiResponse<Object>> sendOtp(@Valid @RequestBody OtpRequest otpRequest) {
        try {
            String otp = otpService.generateAndSaveOtp(otpRequest.getMobile());
            otpService.sendOtp(otpRequest.getMobile(), otp);

            return ResponseEntity.ok(ApiResponse.successMessage("OTP sent successfully to " + otpRequest.getMobile()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send OTP. " + e.getMessage(), 400));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Valid @RequestBody OtpVerifyRequest otpVerifyRequest) {
        boolean isValid = otpService.verifyOtp(otpVerifyRequest.getMobile(), otpVerifyRequest.getOtp());

        if (isValid) {
            return ResponseEntity.ok(ApiResponse.successMessage("OTP verified successfully"));
        } else {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid or expired OTP", 400));
        }
    }

    @PostMapping("/send-email-otp")
    public ResponseEntity<ApiResponse<Object>> sendEmailOtp(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        try {
            // Check if email already exists and is verified
            Optional<User> existingUser = userRepository.findByEmail(emailOtpRequest.getEmail());
            if (existingUser.isPresent() && existingUser.get().getIsVerified()) {
                return ResponseEntity.status(400).body(ApiResponse.error("This email is already verified!", 400));
            }

            String otp = otpService.generateAndSaveEmailOtp(emailOtpRequest.getEmail());
            otpService.sendEmailOtp(emailOtpRequest.getEmail(), otp);

            return ResponseEntity.ok(ApiResponse.successMessage("OTP sent successfully to " + emailOtpRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send email OTP. " + e.getMessage(), 400));
        }
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<ApiResponse<Object>> verifyEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest emailOtpVerifyRequest) {
        try {
            boolean isValid = otpService.verifyEmailOtp(emailOtpVerifyRequest.getEmail(), emailOtpVerifyRequest.getOtp());

            if (isValid) {
                // Update user verification status
                Optional<User> userOpt = userRepository.findByEmail(emailOtpVerifyRequest.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setIsVerified(true);
                    userRepository.save(user);

                    return ResponseEntity.ok(ApiResponse.successMessage("Email verified successfully! Your account is now active."));
                } else {
                    return ResponseEntity.status(400).body(ApiResponse.error("User not found with this email address", 400));
                }
            } else {
                return ResponseEntity.status(400).body(ApiResponse.error("Invalid or expired OTP", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error verifying OTP: " + e.getMessage(), 400));
        }
    }

    @PostMapping("/validate-mobile")
    public ResponseEntity<ApiResponse<Object>> validateMobile(@Valid @RequestBody OtpRequest otpRequest) {
        // Check if mobile number is already registered
        if (userRepository.existsByMobileNumber(otpRequest.getMobile())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Mobile number is already registered!", 400));
        }

        return ResponseEntity.ok(ApiResponse.successMessage("Mobile number is available"));
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Object>> checkEmail(@RequestBody String email) {
        if (email != null && !email.trim().isEmpty() && userRepository.existsByEmail(email)) {
            return ResponseEntity.status(400).body(ApiResponse.error("Email is already in use!", 400));
        }

        return ResponseEntity.ok(ApiResponse.successMessage("Email is available"));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Object>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    // Ensure user exists and is verified before attempting authentication
    Optional<User> userOpt = userRepository.findByMobileNumber(loginRequest.getMobile());
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(400).body(ApiResponse.error("No account found with this mobile number.", 400));
    }

    User user = userOpt.get();
    if (user.getIsVerified() == null || !user.getIsVerified()) {
        return ResponseEntity.status(400).body(ApiResponse.error("Account verification is pending. Please verify your account before signing in.", 400));
    }

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getMobile(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = generateJwtToken(authentication);

    UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

    JwtResponse jwtResp = new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getFullName(),
        userDetails.getMobile(),
        userDetails.getEmail(),
        userDetails.getRole()); // Use actual user role

    return ResponseEntity.ok(ApiResponse.ok(jwtResp, "Authenticated successfully"));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Validate password confirmation
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Password and confirm password do not match!", 400));
        }

        if (userRepository.existsByMobileNumber(signUpRequest.getMobile())) {
        return ResponseEntity.status(400).body(ApiResponse.error("Mobile number is already registered!", 400));
        }

        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.status(400).body(ApiResponse.error("Email is already in use!", 400));
        }

        // Email is required for registration
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
        return ResponseEntity.status(400).body(ApiResponse.error("Email is required for registration!", 400));
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

        return ResponseEntity.ok(ApiResponse.successMessage("User registered successfully!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        // Check if user exists by email
        if (!userRepository.existsByEmail(emailOtpRequest.getEmail())) {
            return ResponseEntity.status(400).body(ApiResponse.error("No account found with this email address!", 400));
        }

        try {
            String otp = otpService.generateAndSaveEmailOtp(emailOtpRequest.getEmail());
            otpService.sendEmailOtp(emailOtpRequest.getEmail(), otp);
            
            return ResponseEntity.ok(ApiResponse.successMessage("Password reset OTP sent successfully to " + emailOtpRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send password reset OTP. " + e.getMessage(), 400));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody EmailResetPasswordRequest resetPasswordRequest) {
        // Verify OTP first
        if (!otpService.verifyEmailOtp(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid or expired OTP", 400));
        }

        // Validate password confirmation
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Password and confirm password do not match!", 400));
        }

        // Find user by email and update password
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(encoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.successMessage("Password reset successfully!"));
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