package com.emojisphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private String id;
    private String fullName;
    private String mobile;
    private String email;
    private String role;
    private List<String> roles; // For backward compatibility

    public JwtResponse(String accessToken, String id, String fullName, String mobile, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
        this.roles = List.of(role); // Convert single role to list for compatibility
    }

    // Legacy constructor for backward compatibility
    public JwtResponse(String accessToken, String id, String name, String mobile, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.fullName = name;
        this.mobile = mobile;
        this.email = email;
        this.roles = roles;
        this.role = roles.isEmpty() ? "USER" : roles.get(0);
    }

    // Getter for backward compatibility
    public String getName() {
        return this.fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }
}