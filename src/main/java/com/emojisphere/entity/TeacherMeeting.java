package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_meetings")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Teacher ID is required")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @NotBlank(message = "Subject title is required")
    @Size(max = 255)
    @Column(name = "subject_title", nullable = false)
    private String subjectTitle;

    @Column(name = "subject_description", columnDefinition = "TEXT")
    private String subjectDescription;

    @Size(max = 500)
    @Column(name = "meeting_url")
    private String meetingUrl;

    @Size(max = 100)
    @Column(name = "meeting_id")
    private String meetingId;

    @Size(max = 100)
    @Column(name = "passcode")
    private String passcode;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotBlank(message = "Time zone is required")
    @Size(max = 100)
    @Column(name = "time_zone", nullable = false)
    private String timeZone;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
