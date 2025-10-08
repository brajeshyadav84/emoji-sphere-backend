package com.emojisphere.controller;

import com.emojisphere.dto.*;
import com.emojisphere.entity.ERole;
import com.emojisphere.entity.Role;
import com.emojisphere.entity.User;
import com.emojisphere.repository.RoleRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

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
            return ResponseEntity.ok(new MessageResponse("OTP verified successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid or expired OTP"));
        }
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
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getMobile(),
                userDetails.getEmail(),
                roles));
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

    // Split name into firstName and lastName
    String[] nameParts = signUpRequest.getName().trim().split(" ", 2);
    String firstName = nameParts[0];
    String lastName = nameParts.length > 1 ? nameParts[1] : "";
    // Create new user's account with correct constructor
    User user = new User(
        firstName,
        lastName,
        signUpRequest.getMobile(),
        encoder.encode(signUpRequest.getPassword()),
        signUpRequest.getAge(),
        signUpRequest.getLocation(),
        signUpRequest.getGender()
    );

        user.setEmail(signUpRequest.getEmail());
        user.setIsVerified(true); // Set as verified since OTP was verified

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
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

        user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

    private String generateJwtToken(Authentication authentication) {
        UserDetailsServiceImpl.UserPrincipal userPrincipal = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getMobile())) // Use mobile as subject
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}