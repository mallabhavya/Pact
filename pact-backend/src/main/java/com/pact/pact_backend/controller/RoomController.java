package com.pact.pact_backend.controller;

import com.pact.pact_backend.dto.request.CreateRoomRequest;
import com.pact.pact_backend.dto.request.JoinRoomRequest;
import com.pact.pact_backend.dto.response.MemberResponse;
import com.pact.pact_backend.dto.response.RoomResponse;
import com.pact.pact_backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roomService.createRoom(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getUserRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roomService.getUserRooms(userDetails.getUsername()));
    }

    @PostMapping("/join")
    public ResponseEntity<RoomResponse> joinRoom(
            @RequestBody JoinRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roomService.joinRoom(request, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomDetails(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomDetails(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberResponse>> getRoomMembers(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomMembers(id));
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        roomService.leaveRoom(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        roomService.deleteRoom(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}