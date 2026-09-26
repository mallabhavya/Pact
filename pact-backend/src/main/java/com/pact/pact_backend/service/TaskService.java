package com.pact.pact_backend.service;

import com.pact.pact_backend.dto.request.CreateTaskRequest;
import com.pact.pact_backend.dto.request.UpdateTaskStatusRequest;
import com.pact.pact_backend.model.ActivityLog;
import com.pact.pact_backend.model.Task;
import com.pact.pact_backend.model.User;
import com.pact.pact_backend.repository.ActivityLogRepository;
import com.pact.pact_backend.repository.TaskRepository;
import com.pact.pact_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public Task createTask(Long roomId, CreateTaskRequest request, String userEmail) {
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Creator user not found: " + userEmail));

        Long creatorId = creator.getId();
        if (creatorId == null) {
            throw new IllegalStateException("Creator user ID cannot be null");
        }

        Task task = Task.builder()
                .roomId(roomId)
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(request.getAssignedTo())
                .createdBy(creatorId)
                .dueDate(request.getDueDate())
                .status(Task.TaskStatus.PENDING)
                .build();

        task = taskRepository.save(task);

        ActivityLog log = activityLogRepository.save(ActivityLog.builder()
                .roomId(roomId)
                .userId(creatorId)
                .action("added")
                .text(task.getTitle())
                .build());

        // Broadcast over WebSocket
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/tasks", task);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/feed", log);

        return task;
    }

    public List<Task> getTasksByRoom(Long roomId) {
        return taskRepository.findByRoomId(roomId);
    }

    public Task updateTaskStatus(Long taskId, UpdateTaskStatusRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        User updater = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Updater user not found: " + userEmail));

        task.setStatus(request.getStatus());
        task = taskRepository.save(task);

        // Broadcast over WebSocket
        messagingTemplate.convertAndSend("/topic/room/" + task.getRoomId() + "/tasks", task);

        if (request.getStatus() == Task.TaskStatus.COMPLETED) {
            ActivityLog log = activityLogRepository.save(ActivityLog.builder()
                    .roomId(task.getRoomId())
                    .userId(updater.getId())
                    .action("completed")
                    .text(task.getTitle())
                    .build());
            messagingTemplate.convertAndSend("/topic/room/" + task.getRoomId() + "/feed", log);
        }

        return task;
    }

    public void deleteTask(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        taskRepository.delete(task);
        // Broadcast task deletion event
        // We can send a null task with ID to signify deletion, or a custom wrapper
        Task deletedStub = new Task();
        deletedStub.setId(taskId);
        deletedStub.setStatus(null); // use status null to indicate delete
        messagingTemplate.convertAndSend("/topic/room/" + task.getRoomId() + "/tasks", deletedStub);
    }
}