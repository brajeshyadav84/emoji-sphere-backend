package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_jokes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Joke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Constructor
    public Joke(String content) {
        this.content = content;
    }
}