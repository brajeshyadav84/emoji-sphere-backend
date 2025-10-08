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
@Table(name = "tbl_daily_questions")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_date", nullable = false, unique = true)
    private LocalDate questionDate;

    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @NotBlank
    @Lob
    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @NotBlank
    @Lob
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "youtube_video_id")
    private String youtubeVideoId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // Constructor
    public DailyQuestion(LocalDate questionDate, Long categoryId, String difficulty, 
                        String question, String answer, String youtubeVideoId) {
        this.questionDate = questionDate;
        this.categoryId = categoryId;
        this.difficulty = difficulty;
        this.question = question;
        this.answer = answer;
        this.youtubeVideoId = youtubeVideoId;
    }
}