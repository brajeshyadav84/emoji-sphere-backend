package com.emojisphere.controller;

import com.emojisphere.dto.ApiResponse;
import com.emojisphere.dto.SubmitFeedbackRequest;
import com.emojisphere.entity.Feedback;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.FeedbackService;
import com.emojisphere.repository.FeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/users/{userId}/feedback")
    public ResponseEntity<ApiResponse<Object>> submitFeedback(@PathVariable Long userId, @Valid @RequestBody SubmitFeedbackRequest request) {
        try {
            // Ensure user exists
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.status(404).body(ApiResponse.error("User not found", 404));
            }

            Feedback saved = feedbackService.saveFeedback(userId, request);

            // Send email notification to admin (fail the request if email fails, similar to OTP behavior)
            try {
                feedbackService.notifyAdmin(saved);
            } catch (Exception e) {
                return ResponseEntity.status(500).body(ApiResponse.error("Failed to send feedback email. " + e.getMessage(), 500));
            }

            return ResponseEntity.ok(ApiResponse.ok(saved, "Feedback submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to submit feedback. " + e.getMessage(), 500));
        }
    }

    @GetMapping("/users/{userId}/feedback")
    public ResponseEntity<ApiResponse<Object>> getUserFeedbacks(@PathVariable Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.status(404).body(ApiResponse.error("User not found", 404));
            }

            List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);

            return ResponseEntity.ok(ApiResponse.ok(feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch feedbacks. " + e.getMessage(), 500));
        }
    }
}
