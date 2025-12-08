package com.emojisphere.service;

import com.emojisphere.dto.TeacherMeetingRequestDTO;
import com.emojisphere.dto.TeacherMeetingResponseDTO;
import com.emojisphere.entity.TeacherMeeting;
import com.emojisphere.repository.TeacherMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherMeetingService {

    private final TeacherMeetingRepository teacherMeetingRepository;

    /**
     * Create a new meeting
     */
    @Transactional
    public TeacherMeetingResponseDTO createMeeting(Long teacherId, TeacherMeetingRequestDTO requestDTO) {
        TeacherMeeting meeting = new TeacherMeeting();
        meeting.setTeacherId(teacherId);
        meeting.setSubjectTitle(requestDTO.getSubjectTitle());
        meeting.setSubjectDescription(requestDTO.getSubjectDescription());
        meeting.setMeetingUrl(requestDTO.getMeetingUrl());
        meeting.setMeetingId(requestDTO.getMeetingId());
        meeting.setPasscode(requestDTO.getPasscode());
        meeting.setStartTime(requestDTO.getStartTime());
        meeting.setEndTime(requestDTO.getEndTime());
        meeting.setTimeZone(requestDTO.getTimeZone());

        TeacherMeeting savedMeeting = teacherMeetingRepository.save(meeting);
        return mapToResponseDTO(savedMeeting);
    }

    /**
     * Update an existing meeting
     */
    @Transactional
    public TeacherMeetingResponseDTO updateMeeting(Long meetingId, Long teacherId, TeacherMeetingRequestDTO requestDTO) {
        TeacherMeeting meeting = teacherMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + meetingId));

        // Verify that the meeting belongs to the teacher
        if (!meeting.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Meeting does not belong to this teacher");
        }

        meeting.setSubjectTitle(requestDTO.getSubjectTitle());
        meeting.setSubjectDescription(requestDTO.getSubjectDescription());
        meeting.setMeetingUrl(requestDTO.getMeetingUrl());
        meeting.setMeetingId(requestDTO.getMeetingId());
        meeting.setPasscode(requestDTO.getPasscode());
        meeting.setStartTime(requestDTO.getStartTime());
        meeting.setEndTime(requestDTO.getEndTime());
        meeting.setTimeZone(requestDTO.getTimeZone());

        TeacherMeeting updatedMeeting = teacherMeetingRepository.save(meeting);
        return mapToResponseDTO(updatedMeeting);
    }

    /**
     * Delete a meeting
     */
    @Transactional
    public void deleteMeeting(Long meetingId, Long teacherId) {
        TeacherMeeting meeting = teacherMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + meetingId));

        // Verify that the meeting belongs to the teacher
        if (!meeting.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Meeting does not belong to this teacher");
        }

        teacherMeetingRepository.delete(meeting);
    }

    /**
     * Get a single meeting by ID
     */
    public TeacherMeetingResponseDTO getMeetingById(Long meetingId, Long teacherId) {
        TeacherMeeting meeting = teacherMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + meetingId));

        // Verify that the meeting belongs to the teacher
        if (!meeting.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Meeting does not belong to this teacher");
        }

        return mapToResponseDTO(meeting);
    }

    /**
     * Get all meetings for a teacher
     */
    public List<TeacherMeetingResponseDTO> getAllMeetingsByTeacherId(Long teacherId) {
        List<TeacherMeeting> meetings = teacherMeetingRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
        return meetings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming meetings for a teacher
     */
    public List<TeacherMeetingResponseDTO> getUpcomingMeetings(Long teacherId) {
        LocalDateTime now = LocalDateTime.now();
        List<TeacherMeeting> meetings = teacherMeetingRepository.findUpcomingMeetingsByTeacherId(teacherId, now);
        return meetings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get meetings within a date range
     */
    public List<TeacherMeetingResponseDTO> getMeetingsByDateRange(Long teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        List<TeacherMeeting> meetings = teacherMeetingRepository.findMeetingsByTeacherIdAndDateRange(teacherId, startDate, endDate);
        return meetings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get ongoing meetings for a teacher
     */
    public List<TeacherMeetingResponseDTO> getOngoingMeetings(Long teacherId) {
        LocalDateTime now = LocalDateTime.now();
        List<TeacherMeeting> meetings = teacherMeetingRepository.findOngoingMeetingsByTeacherId(teacherId, now);
        return meetings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all public meetings (for students to view)
     */
    public List<TeacherMeetingResponseDTO> getAllPublicMeetings() {
        List<TeacherMeeting> meetings = teacherMeetingRepository.findAll();
        return meetings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map entity to response DTO
     */
    private TeacherMeetingResponseDTO mapToResponseDTO(TeacherMeeting meeting) {
        TeacherMeetingResponseDTO dto = new TeacherMeetingResponseDTO();
        dto.setId(meeting.getId());
        dto.setTeacherId(meeting.getTeacherId());
        dto.setSubjectTitle(meeting.getSubjectTitle());
        dto.setSubjectDescription(meeting.getSubjectDescription());
        dto.setMeetingUrl(meeting.getMeetingUrl());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setPasscode(meeting.getPasscode());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setTimeZone(meeting.getTimeZone());
        dto.setCreatedAt(meeting.getCreatedAt());
        dto.setUpdatedAt(meeting.getUpdatedAt());
        dto.setStatus(getMeetingStatus(meeting.getStartTime(), meeting.getEndTime()));
        return dto;
    }

    /**
     * Determine meeting status
     */
    private String getMeetingStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return "upcoming";
        } else if (now.isAfter(endTime)) {
            return "completed";
        } else {
            return "live";
        }
    }
}
