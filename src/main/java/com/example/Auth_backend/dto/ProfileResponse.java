package com.example.Auth_backend.dto;

import lombok.Setter;

public class ProfileResponse {

    private String name;
    private String email;
    private String role;
    @Setter
    private String profileImage;

    public ProfileResponse(String name, String email, String role, String profileImage) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getProfileImage() {
        return profileImage;
    }

}