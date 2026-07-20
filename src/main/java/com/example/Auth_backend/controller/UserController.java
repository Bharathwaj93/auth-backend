package com.example.Auth_backend.controller;

import com.example.Auth_backend.service.UserService;
import com.example.Auth_backend.dto.ProfileResponse;
import com.example.Auth_backend.dto.UpdateProfileRequest;
import com.example.Auth_backend.entity.User;
import com.example.Auth_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.Auth_backend.dto.ChangePasswordRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(Authentication authentication) {

        String email = authentication.getName();

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImage()
        );

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(userService.getStats());
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        System.out.println("=== CHANGE PASSWORD CONTROLLER ===");
        System.out.println(authentication.getName());

        userService.changePassword(authentication.getName(), request);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            User user = (User) userRepository
                    .findByEmail(authentication.getName())
                    .orElseThrow();

            String originalName = file.getOriginalFilename();

            System.out.println(originalName);

            String extension = "";

            String original = file.getOriginalFilename();

            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }

            String fileName = System.currentTimeMillis() + extension;

            Path uploadPath = Paths.get(
                    "C:\\Users\\BHARATH\\Downloads\\Auth-backend\\Auth-backend\\src\\main\\resources\\static\\images");

            System.out.println("Upload Path = " + uploadPath.toAbsolutePath());
            System.out.println("Folder Exists = " + Files.exists(uploadPath));
            System.out.println(System.getProperty("user.dir"));


            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            System.out.println("Original Name = " + file.getOriginalFilename());
            System.out.println("Content Type = " + file.getContentType());
            System.out.println("File Size = " + file.getSize());

            Path destination = uploadPath.resolve(fileName);

            System.out.println("Destination = " + destination.toAbsolutePath());

            System.out.println(fileName);



            file.transferTo(destination.toFile());


            user.setProfileImage(fileName);
            userRepository.save(user);

            return ResponseEntity.ok(fileName);

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Message: " + e.getMessage());
            System.out.println("Class: " + e.getClass().getName());

            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName) {

        try {

            Path imagePath = Paths.get(
                            "C:\\Users\\BHARATH\\Downloads\\Auth-backend\\Auth-backend\\src\\main\\resources\\static\\images")
                    .resolve(fileName);

            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}



