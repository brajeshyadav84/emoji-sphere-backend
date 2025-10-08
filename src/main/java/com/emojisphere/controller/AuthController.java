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

import java.util.Date;

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

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpiration;

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
                "USER")); // Default role
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

        // Verify OTP before registration
        if (!otpService.isOtpVerified(signUpRequest.getMobile())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Please verify your mobile number with OTP first!"));
        }

        // Create new user's account with new constructor
        User user = new User(
            signUpRequest.getMobile(),
            signUpRequest.getFullName(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getAge(),
            signUpRequest.getCountry(),
            signUpRequest.getGender()
        );

        user.setEmail(signUpRequest.getEmail());
        user.setIsVerified(true); // Set as verified since OTP was verified
        user.setRole("USER"); // Default role

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody OtpRequest otpRequest) {
        // Check if user exists
        if (!userRepository.existsByMobileNumber(otpRequest.getMobile())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: No account found with this mobile number!"));
        }

        try {
            String otp = otpService.generateAndSaveOtp(otpRequest.getMobile());
            otpService.sendOtp(otpRequest.getMobile(), otp);
            
            return ResponseEntity.ok(new MessageResponse("Password reset OTP sent successfully to " + otpRequest.getMobile()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to send password reset OTP. " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        // Verify OTP first
        if (!otpService.verifyOtp(resetPasswordRequest.getMobile(), resetPasswordRequest.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid or expired OTP"));
        }

        // Validate password confirmation
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Password and confirm password do not match!"));
        }

        // Find user and update password
        User user = userRepository.findByMobileNumber(resetPasswordRequest.getMobile())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(encoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

    private String generateJwtToken(Authentication authentication) {
        UserDetailsServiceImpl.UserPrincipal userPrincipal = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

        SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        return Jwts.builder()
                .setSubject((userPrincipal.getMobile())) // Use mobile as subject
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}