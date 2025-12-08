package com.emojisphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherMeetingResponseDTO {

    private Long id;
    private Long teacherId;
    private String subjectTitle;
    private String subjectDescription;
    private String meetingUrl;
    private String meetingId;
    private String passcode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeZone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; // "upcoming", "live", "completed"
}
