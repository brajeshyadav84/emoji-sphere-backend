package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_groups")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 50)
    @Column(name = "email")
    private String email;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @Size(max = 10)
    @Column(name = "emoji")
    private String emoji;

    @Size(max = 20)
    @Column(name = "privacy")
    private String privacy = "PUBLIC";

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GroupMember> members = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User creator;

    public Group(String name, String emoji, String description, String privacy, Long createdBy) {
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.privacy = privacy;
        this.createdBy = createdBy;
    }
}