package com.example.Auth_backend.service;
import com.example.Auth_backend.dto.ProfileResponse;
import com.example.Auth_backend.dto.UpdateProfileRequest;
import com.example.Auth_backend.entity.User;
import com.example.Auth_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import com.example.Auth_backend.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;

import javax.swing.*;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public List<User> getRecentUsers() {

        List<User> users = userRepository.findAll();

        users.sort((u1, u2) -> Long.compare(u2.getId(), u1.getId()));

        return users.stream()
                .limit(5)
                .toList();
    }
    @Override
    public User updateUser(Long id, User updatedUser) {

        System.out.println("SERVICE CALLED");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {

        System.out.println("UPDATE API CALLED");

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return new ProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImage()
        );
    }
    @Override
    public Map<String, Long> getStats() {

        Map<String, Long> stats = new HashMap<>();

        long totalUsers = userRepository.count();

        long totalAdmins = userRepository.findAll()
                .stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .count();

        long normalUsers = totalUsers - totalAdmins;

        stats.put("totalUsers", totalUsers);
        stats.put("totalAdmins", totalAdmins);
        stats.put("normalUsers", normalUsers);

        return stats;
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {

        System.out.println("=== CHANGE PASSWORD SERVICE ===");

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User Found: " + user.getEmail());

        boolean isMatch = passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        );

        System.out.println("Current Password Match: " + isMatch);

        if (!isMatch) {
            throw new RuntimeException("Current password is incorrect");
        }

        System.out.println("New Password: " + request.getNewPassword());
        System.out.println("Confirm Password: " + request.getConfirmPassword());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and Confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        System.out.println("Password Updated Successfully");
    }
    @Override
    public void forgotPassword(String email) {

        System.out.println("Service Method Called");

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        System.out.println("User Found = " + user.getEmail());

        String otp = String.format("%06d", new Random().nextInt(999999));

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        System.out.println("OTP = " + otp);
        System.out.println("Sending Email...");

        emailService.sendOtp(user.getEmail(), otp);

        System.out.println("Email Sent Successfully");
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (user.getOtp() == null) {
            throw new RuntimeException("OTP not generated");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new RuntimeException("OTP expired");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        return true;
    }

    @Override
    public void resetPassword(String email,
                              String newPassword,
                              String confirmPassword) {

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear OTP after successful password reset
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
    }
}