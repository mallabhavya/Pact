package com.pact.pact_backend.dto.request;
import lombok.Data;
@Data
public class CreateRoomRequest {
    private String name;
    private String description;
}
