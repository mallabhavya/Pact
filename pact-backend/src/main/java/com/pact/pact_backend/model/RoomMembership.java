package com.pact.pact_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long roomId;
}