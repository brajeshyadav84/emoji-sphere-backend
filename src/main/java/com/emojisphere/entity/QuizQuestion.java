package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_quiz_questions")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @NotBlank
    @Lob
    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Size(max = 500)
    @Column(name = "option1")
    private String option1;

    @Size(max = 500)
    @Column(name = "option2")
    private String option2;

    @Size(max = 500)
    @Column(name = "option3")
    private String option3;

    @Size(max = 500)
    @Column(name = "option4")
    private String option4;

    @NotBlank
    @Size(max = 500)
    @Column(name = "correct_option", nullable = false)
    private String correctOption;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    // Constructor
    public QuizQuestion(Long quizId, String question, String option1, String option2, 
                       String option3, String option4, String correctOption) {
        this.quizId = quizId;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
    }
}