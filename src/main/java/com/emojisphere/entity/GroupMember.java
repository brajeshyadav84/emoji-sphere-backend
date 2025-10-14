package com.emojisphere.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_group_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "status")
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    @JsonBackReference
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
    
    // Constructor for easier creation
    public GroupMember(Long groupId, Long userId, Integer age, String status) {
        this.groupId = groupId;
        this.userId = userId;
        this.age = age;
        this.status = status;
        this.joinedAt = LocalDateTime.now();
    }
}