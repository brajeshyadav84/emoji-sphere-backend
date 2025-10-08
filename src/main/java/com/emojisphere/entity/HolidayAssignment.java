package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_holiday_assignments")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "grade_id")
    private Long gradeId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "holiday_type", nullable = false)
    private String holidayType;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", insertable = false, updatable = false)
    private Grade grade;

    // Constructor
    public HolidayAssignment(String title, Long gradeId, String holidayType, 
                           LocalDate dueDate, String description) {
        this.title = title;
        this.gradeId = gradeId;
        this.holidayType = holidayType;
        this.dueDate = dueDate;
        this.description = description;
    }
}