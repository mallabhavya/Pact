package com.pact.pact_backend.controller;

import com.pact.pact_backend.dto.request.CreateTaskRequest;
import com.pact.pact_backend.dto.request.UpdateTaskStatusRequest;
import com.pact.pact_backend.model.Task;
import com.pact.pact_backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/rooms/{roomId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable Long roomId,
            @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.createTask(roomId, request, userDetails.getUsername()));
    }

    @GetMapping("/rooms/{roomId}/tasks")
    public ResponseEntity<List<Task>> getRoomTasks(@PathVariable Long roomId) {
        return ResponseEntity.ok(taskService.getTasksByRoom(roomId));
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(taskId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}