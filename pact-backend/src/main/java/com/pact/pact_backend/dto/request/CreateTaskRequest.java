package com.pact.pact_backend.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private Long assignedTo;
    private LocalDateTime dueDate;
}