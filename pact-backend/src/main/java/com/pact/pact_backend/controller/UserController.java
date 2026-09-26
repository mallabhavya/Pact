package com.pact.pact_backend.controller;

import com.pact.pact_backend.model.User;
import com.pact.pact_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null); // Don't expose password
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody User profileData) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileData.getName() != null) user.setName(profileData.getName());
        if (profileData.getHandle() != null) user.setHandle(profileData.getHandle());
        if (profileData.getAvatar() != null) user.setAvatar(profileData.getAvatar());
        if (profileData.getStatus() != null) user.setStatus(profileData.getStatus());
        if (profileData.getBio() != null) user.setBio(profileData.getBio());
        if (profileData.getLocation() != null) user.setLocation(profileData.getLocation());
        if (profileData.getInsta() != null) user.setInsta(profileData.getInsta());
        if (profileData.getSpotify() != null) user.setSpotify(profileData.getSpotify());

        User updated = userRepository.save(user);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }
}
