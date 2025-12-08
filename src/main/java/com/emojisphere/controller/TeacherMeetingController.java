package com.emojisphere.controller;

import com.emojisphere.dto.TeacherMeetingRequestDTO;
import com.emojisphere.dto.TeacherMeetingResponseDTO;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.TeacherMeetingService;
import com.emojisphere.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher-meetings")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class TeacherMeetingController {

    private final TeacherMeetingService teacherMeetingService;
    private final UserRepository userRepository;

    /**
     * Create a new meeting
     * POST /api/teacher-meetings
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createMeeting(
            @Valid @RequestBody TeacherMeetingRequestDTO requestDTO) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            TeacherMeetingResponseDTO meeting = teacherMeetingService.createMeeting(teacherId, requestDTO);
            response.put("success", true);
            response.put("message", "Meeting created successfully");
            response.put("data", meeting);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create meeting: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Update an existing meeting
     * PUT /api/teacher-meetings/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMeeting(
            @PathVariable("id") Long meetingId,
            @Valid @RequestBody TeacherMeetingRequestDTO requestDTO) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            TeacherMeetingResponseDTO meeting = teacherMeetingService.updateMeeting(meetingId, teacherId, requestDTO);
            response.put("success", true);
            response.put("message", "Meeting updated successfully");
            response.put("data", meeting);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete a meeting
     * DELETE /api/teacher-meetings/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMeeting(
            @PathVariable("id") Long meetingId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            teacherMeetingService.deleteMeeting(meetingId, teacherId);
            response.put("success", true);
            response.put("message", "Meeting deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get a single meeting by ID
     * GET /api/teacher-meetings/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMeetingById(
            @PathVariable("id") Long meetingId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            TeacherMeetingResponseDTO meeting = teacherMeetingService.getMeetingById(meetingId, teacherId);
            response.put("success", true);
            response.put("data", meeting);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all meetings for a teacher
     * GET /api/teacher-meetings
     */
    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllMeetings() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            List<TeacherMeetingResponseDTO> meetings = teacherMeetingService.getAllMeetingsByTeacherId(teacherId);
            response.put("success", true);
            response.put("data", meetings);
            response.put("count", meetings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch meetings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all public meetings (for students) - no teacher restriction
     * GET /api/teacher-meetings/public
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getAllPublicMeetings() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // Get all meetings from all teachers
            List<TeacherMeetingResponseDTO> meetings = teacherMeetingService.getAllPublicMeetings();
            response.put("success", true);
            response.put("data", meetings);
            response.put("count", meetings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch meetings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get upcoming meetings for a teacher
     * GET /api/teacher-meetings/upcoming
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUpcomingMeetings() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            List<TeacherMeetingResponseDTO> meetings = teacherMeetingService.getUpcomingMeetings(teacherId);
            response.put("success", true);
            response.put("data", meetings);
            response.put("count", meetings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch upcoming meetings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get ongoing meetings for a teacher
     * GET /api/teacher-meetings/ongoing
     */
    @GetMapping("/ongoing")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOngoingMeetings() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            List<TeacherMeetingResponseDTO> meetings = teacherMeetingService.getOngoingMeetings(teacherId);
            response.put("success", true);
            response.put("data", meetings);
            response.put("count", meetings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch ongoing meetings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get meetings within a date range
     * GET /api/teacher-meetings/date-range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMeetingsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = getCurrentUserId();
            List<TeacherMeetingResponseDTO> meetings = teacherMeetingService.getMeetingsByDateRange(teacherId, startDate, endDate);
            response.put("success", true);
            response.put("data", meetings);
            response.put("count", meetings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch meetings by date range: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper method to get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        
        // Get the mobile number from the principal
        Long mobile = userDetails.getId();
        
        // Look up the user by mobile number to get the actual user ID
        User user = userRepository.findById(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getId();
    }
}
