package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) throws Exception {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User updateUser(String email, String fullName, String phoneNumber, MultipartFile avatar) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));


        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }


        if (avatar != null && !avatar.isEmpty()) {
            String currentDate = LocalDate.now().toString(); // ví dụ: "2025-06-29"
            String originalFileName = Paths.get(avatar.getOriginalFilename()).getFileName().toString();
            String timestamp = String.valueOf(System.currentTimeMillis());

            String fileName = timestamp + "_" + originalFileName;

            Path uploadDir = Paths.get("uploads", "avatars", currentDate);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setAvatar("/uploads/avatars/" + currentDate + "/" + fileName);
        }

        return userRepository.save(user);
    }

    // Helper để lấy đuôi file
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }


}
