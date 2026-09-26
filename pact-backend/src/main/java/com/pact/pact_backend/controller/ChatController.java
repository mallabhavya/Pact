package com.pact.pact_backend.controller;

import com.pact.pact_backend.model.ActivityLog;
import com.pact.pact_backend.model.Message;
import com.pact.pact_backend.repository.ActivityLogRepository;
import com.pact.pact_backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.pact.pact_backend.model.User;
import com.pact.pact_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/rooms/{roomId}")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId));
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> postMessage(
            @PathVariable Long roomId,
            @RequestBody Message request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        request.setRoomId(roomId);
        request.setUserId(user.getId());
        Message saved = messageRepository.save(request);
        
        // Broadcast over WebSocket
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/messages", saved);
        
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/messages/{messageId}/react")
    public ResponseEntity<Message> reactToMessage(
            @PathVariable Long roomId,
            @PathVariable Long messageId,
            @RequestBody java.util.Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
                
        String emoji = payload.get("emoji");
        String userIdStr = String.valueOf(user.getId());
        
        if (emoji == null || emoji.isEmpty()) {
            msg.getReactions().remove(userIdStr);
        } else {
            msg.getReactions().put(userIdStr, emoji);
        }
        
        Message saved = messageRepository.save(msg);
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/messages", saved);
        
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<ActivityLog>> getFeed(@PathVariable Long roomId) {
        return ResponseEntity.ok(activityLogRepository.findByRoomIdOrderByCreatedAtDesc(roomId));
    }

    @PostMapping("/feed/{feedId}/react")
    public ResponseEntity<ActivityLog> reactToFeed(
            @PathVariable Long roomId,
            @PathVariable Long feedId,
            @RequestBody java.util.Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ActivityLog log = activityLogRepository.findById(feedId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
                
        String emoji = payload.get("emoji");
        String userIdStr = String.valueOf(user.getId());
        
        if (emoji == null || emoji.isEmpty()) {
            log.getReactions().remove(userIdStr);
        } else {
            log.getReactions().put(userIdStr, emoji);
        }
        
        ActivityLog saved = activityLogRepository.save(log);
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/feed", saved);
        
        return ResponseEntity.ok(saved);
    }
}
