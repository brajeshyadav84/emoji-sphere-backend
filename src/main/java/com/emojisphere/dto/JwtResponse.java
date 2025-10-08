package com.emojisphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String name, String mobile, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.roles = roles;
    }
}