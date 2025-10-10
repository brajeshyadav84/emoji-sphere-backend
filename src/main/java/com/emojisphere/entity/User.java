package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

@Entity
@Table(name = "tbl_users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "mobile_number")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Size(max = 10)
    @Column(name = "gender")
    private String gender;

    @Size(max = 100)
    @Column(name = "country")
    private String country;

    @Size(max = 255)
    @Column(name = "school_name")
    private String schoolName;

    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Size(max = 20)
    @Column(name = "role")
    private String role = "USER";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Legacy support for existing code
    @Transient
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    // Group relationships
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Group> createdGroups = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GroupMember> groupMemberships = new HashSet<>();

    // Constructors
    public User(String mobileNumber, String passwordHash) {
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
    }

    public User(String mobileNumber, String fullName, String passwordHash, Integer age, String country, String gender) {
        this.mobileNumber = mobileNumber;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.age = age;
        this.country = country;
        this.gender = gender;
    }

    public User(String mobileNumber, String fullName, String passwordHash, Integer age, String country, String gender, String schoolName) {
        this.mobileNumber = mobileNumber;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.age = age;
        this.country = country;
        this.gender = gender;
        this.schoolName = schoolName;
    }

    // Getter methods for compatibility
    public String getUserId() {
        return this.mobileNumber;
    }

    public void setUserId(String userId) {
        this.mobileNumber = userId;
    }

    // Getter and setter for password compatibility
    public String getPassword() {
        return this.passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    // Getter for name compatibility
    public String getName() {
        return this.fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }
}