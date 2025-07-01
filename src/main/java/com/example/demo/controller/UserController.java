package com.example.demo.controller;

import com.example.demo.Config.JwtUtil;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            String email = userDetails.getUsername(); // lấy từ JWT
            User updatedUser = userService.updateUser(email, fullName, phoneNumber, avatar);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userDetails.getUsername(), request);
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
