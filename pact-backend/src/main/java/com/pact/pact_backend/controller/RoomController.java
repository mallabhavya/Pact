package com.pact.pact_backend.controller;

import com.pact.pact_backend.model.Room;
import com.pact.pact_backend.model.RoomMembership;
import com.pact.pact_backend.repository.RoomMembershipRepository;
import com.pact.pact_backend.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.pact.pact_backend.model.User;
import com.pact.pact_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomMembershipRepository membershipRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/rooms - Create a room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Object> payload) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        String name = (String) payload.get("name");
        String description = (String) payload.get("description");

        String inviteCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Room room = Room.builder()
                .name(name)
                .description(description)
                .inviteCode(inviteCode)
                .createdBy(userId)
                .build();

        Room savedRoom = roomRepository.save(room);

        // Automatically add creator as a member
        RoomMembership membership = RoomMembership.builder()
                .userId(userId)
                .roomId(savedRoom.getId())
                .build();
        membershipRepository.save(membership);

        return ResponseEntity.ok(savedRoom);
    }

    // GET /api/rooms - List user's rooms
    @GetMapping
    public ResponseEntity<List<Room>> getUserRooms() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        List<RoomMembership> memberships = membershipRepository.findByUserId(userId);
        List<Long> roomIds = memberships.stream().map(RoomMembership::getRoomId).collect(Collectors.toList());
        List<Room> rooms = roomRepository.findAllById(roomIds);
        return ResponseEntity.ok(rooms);
    }

    // POST /api/rooms/join - Join an existing room via invite code
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody Map<String, String> payload) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        String inviteCode = payload.get("inviteCode");

        Room room = roomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Room not found with code: " + inviteCode));

        if (membershipRepository.existsByUserIdAndRoomId(userId, room.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Already a member of this room"));
        }

        RoomMembership membership = RoomMembership.builder()
                .userId(userId)
                .roomId(room.getId())
                .build();
        membershipRepository.save(membership);

        return ResponseEntity.ok(room);
    }
}