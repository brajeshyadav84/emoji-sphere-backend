package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OtpVerification(String mobile, String otp, LocalDateTime expiresAt) {
        this.mobile = mobile;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.verified = false;
    }

    public OtpVerification(String email, String otp, LocalDateTime expiresAt, boolean isEmail) {
        this.email = email;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.verified = false;
    }
}