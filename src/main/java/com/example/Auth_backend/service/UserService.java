package com.example.Auth_backend.service;

import com.example.Auth_backend.dto.ChangePasswordRequest;
import com.example.Auth_backend.entity.User;
import com.example.Auth_backend.service.UserService;
import java.util.List;
import com.example.Auth_backend.dto.UpdateProfileRequest;
import com.example.Auth_backend.dto.ProfileResponse;
import java.util.Map;

public interface UserService {

    List<User> getAllUsers();

    List<User> getRecentUsers();

    User getUserById(Long id);

    User updateUser(Long id, User updatedUser);

    void deleteUser(Long id);

    ProfileResponse updateProfile(String email, UpdateProfileRequest request);

    Map<String, Long> getStats();

    void changePassword(String email, ChangePasswordRequest request);

    void forgotPassword(String email);

    boolean verifyOtp(String email, String otp);

    void resetPassword(String email, String newPassword, String confirmPassword);

}
