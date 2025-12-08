package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherMeetingRequestDTO {

    @NotBlank(message = "Subject title is required")
    @Size(max = 255, message = "Subject title must not exceed 255 characters")
    private String subjectTitle;

    @Size(max = 1000, message = "Subject description must not exceed 1000 characters")
    private String subjectDescription;

    @Size(max = 500, message = "Meeting URL must not exceed 500 characters")
    private String meetingUrl;

    @Size(max = 100, message = "Meeting ID must not exceed 100 characters")
    private String meetingId;

    @Size(max = 100, message = "Passcode must not exceed 100 characters")
    private String passcode;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Time zone is required")
    @Size(max = 100, message = "Time zone must not exceed 100 characters")
    private String timeZone;
}
