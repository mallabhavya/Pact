package com.pact.pact_backend.service;

import com.pact.pact_backend.dto.request.CreateRoomRequest;
import com.pact.pact_backend.dto.request.JoinRoomRequest;
import com.pact.pact_backend.dto.response.MemberResponse;
import com.pact.pact_backend.dto.response.RoomResponse;
import com.pact.pact_backend.model.ActivityLog;
import com.pact.pact_backend.model.Room;
import com.pact.pact_backend.model.RoomMembership;
import com.pact.pact_backend.model.User;
import com.pact.pact_backend.repository.ActivityLogRepository;
import com.pact.pact_backend.repository.RoomMembershipRepository;
import com.pact.pact_backend.repository.RoomRepository;
import com.pact.pact_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMembershipRepository roomMembershipRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    public List<MemberResponse> getRoomMembers(Long roomId) {
        List<RoomMembership> memberships = roomMembershipRepository.findByRoomId(roomId);

        return memberships.stream().map(membership -> {
            User user = userRepository.findById(membership.getUserId()).orElse(null);
            
            String userEmail = user != null ? user.getEmail() : null;
            String username = userEmail != null ? userEmail : "User #" + membership.getUserId();

            return MemberResponse.builder()
                    .id(membership.getId())
                    .userId(membership.getUserId())
                    .email(userEmail)
                    .username(username)
                    .role("MEMBER") // Default fallback if getRole() is not yet in RoomMembership
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .inviteCode(inviteCode)
                .createdBy(user.getId())
                .build();

        room = roomRepository.save(room);

        RoomMembership membership = RoomMembership.builder()
                .roomId(room.getId())
                .userId(user.getId())
                // .role("ADMIN") // If RoomMembership had role
                .build();
        roomMembershipRepository.save(membership);

        activityLogRepository.save(ActivityLog.builder()
                .roomId(room.getId())
                .userId(user.getId())
                .action("created")
                .text("Room created")
                .build());

        return mapToRoomResponse(room);
    }

    public List<RoomResponse> getUserRooms(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        List<RoomMembership> memberships = roomMembershipRepository.findByUserId(user.getId());
        
        return memberships.stream().map(m -> {
            Room room = roomRepository.findById(m.getRoomId()).orElseThrow();
            return mapToRoomResponse(room);
        }).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse joinRoom(JoinRoomRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        Room room = roomRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        if (roomMembershipRepository.existsByRoomIdAndUserId(room.getId(), user.getId())) {
            throw new RuntimeException("Already a member of this room");
        }

        RoomMembership membership = RoomMembership.builder()
                .roomId(room.getId())
                .userId(user.getId())
                .build();
        roomMembershipRepository.save(membership);

        activityLogRepository.save(ActivityLog.builder()
                .roomId(room.getId())
                .userId(user.getId())
                .action("joined")
                .text("Joined the room")
                .build());

        return mapToRoomResponse(room);
    }

    public RoomResponse getRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        return mapToRoomResponse(room);
    }

    private RoomResponse mapToRoomResponse(Room room) {
        List<MemberResponse> members = getRoomMembers(room.getId());
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .inviteCode(room.getInviteCode())
                .createdAt(room.getCreatedAt())
                .members(members)
                .build();
    }

    @Transactional
    public void leaveRoom(Long roomId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        RoomMembership membership = roomMembershipRepository.findByRoomId(roomId).stream()
            .filter(m -> m.getUserId().equals(user.getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Not a member of this room"));
            
        roomMembershipRepository.delete(membership);
    }

    @Transactional
    public void deleteRoom(Long roomId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        
        if (!room.getCreatedBy().equals(user.getId())) {
            throw new RuntimeException("Only the room creator can delete the room");
        }

        // Just delete the memberships and room for simplicity. 
        // In a real app we'd delete messages, tasks, logs or use DB cascade.
        List<RoomMembership> memberships = roomMembershipRepository.findByRoomId(roomId);
        roomMembershipRepository.deleteAll(memberships);
        roomRepository.delete(room);
    }
}