package com.emojisphere.controller;

import com.emojisphere.dto.MessageResponse;
import com.emojisphere.dto.UpdateProfileRequest;
import com.emojisphere.dto.UserProfileResponse;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            
            Optional<User> userOptional = userRepository.findByMobileNumber(userDetails.getMobile());
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getMobileNumber(),
                user.getFullName(),
                user.getAge(),
                user.getGender(),
                user.getCountry(),
                user.getSchoolName(),
                user.getEmail(),
                user.getIsVerified(),
                user.getIsActive(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to fetch user profile. " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            
            Optional<User> userOptional = userRepository.findByMobileNumber(userDetails.getMobile());
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new MessageResponse("Error: User not found!"));
            }
            
            User user = userOptional.get();
            
            // Check if email is being updated and if it's already in use by another user
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(updateRequest.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Error: Email is already in use by another user!"));
                }
            }
            
            // Update user fields
            if (updateRequest.getFullName() != null) {
                user.setFullName(updateRequest.getFullName());
            }
            
            if (updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }
            
            if (updateRequest.getAge() != null) {
                user.setAge(updateRequest.getAge());
            }
            
            if (updateRequest.getCountry() != null) {
                user.setCountry(updateRequest.getCountry());
            }
            
            if (updateRequest.getGender() != null) {
                user.setGender(updateRequest.getGender());
            }
            
            if (updateRequest.getSchoolName() != null) {
                user.setSchoolName(updateRequest.getSchoolName());
            }
            
            userRepository.save(user);
            
            // Return updated profile
            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getMobileNumber(),
                user.getFullName(),
                user.getAge(),
                user.getGender(),
                user.getCountry(),
                user.getSchoolName(),
                user.getEmail(),
                user.getIsVerified(),
                user.getIsActive(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to update user profile. " + e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new MessageResponse("Error: User not found!"));
            }
            
            User user = userOptional.get();
            
            // Return limited public profile information
            UserProfileResponse response = new UserProfileResponse();
            response.setId(user.getId());
            response.setFullName(user.getFullName());
            response.setAge(user.getAge());
            response.setGender(user.getGender());
            response.setCountry(user.getCountry());
            response.setSchoolName(user.getSchoolName());
            response.setIsActive(user.getIsActive());
            response.setCreatedAt(user.getCreatedAt());
            
            // Don't expose sensitive information like email, mobile, etc. for public profile
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to fetch user profile. " + e.getMessage()));
        }
    }
}