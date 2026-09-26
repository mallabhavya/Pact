package com.pact.pact_backend.dto.request;

import com.pact.pact_backend.model.Task.TaskStatus;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    private TaskStatus status;
}