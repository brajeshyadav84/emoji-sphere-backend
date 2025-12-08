package com.emojisphere.repository;

import com.emojisphere.entity.TeacherMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeacherMeetingRepository extends JpaRepository<TeacherMeeting, Long> {
    
    /**
     * Find all meetings by teacher ID
     */
    List<TeacherMeeting> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    
    /**
     * Find upcoming meetings for a teacher
     */
    @Query("SELECT tm FROM TeacherMeeting tm WHERE tm.teacherId = :teacherId AND tm.startTime > :currentTime ORDER BY tm.startTime ASC")
    List<TeacherMeeting> findUpcomingMeetingsByTeacherId(@Param("teacherId") Long teacherId, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find meetings within a date range
     */
    @Query("SELECT tm FROM TeacherMeeting tm WHERE tm.teacherId = :teacherId AND tm.startTime BETWEEN :startDate AND :endDate ORDER BY tm.startTime ASC")
    List<TeacherMeeting> findMeetingsByTeacherIdAndDateRange(@Param("teacherId") Long teacherId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find ongoing meetings for a teacher
     */
    @Query("SELECT tm FROM TeacherMeeting tm WHERE tm.teacherId = :teacherId AND tm.startTime <= :currentTime AND tm.endTime >= :currentTime")
    List<TeacherMeeting> findOngoingMeetingsByTeacherId(@Param("teacherId") Long teacherId, @Param("currentTime") LocalDateTime currentTime);
}
