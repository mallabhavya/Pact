package com.pact.pact_backend.dto.response;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private LocalDateTime createdAt;
    private List<MemberResponse> members;
}
