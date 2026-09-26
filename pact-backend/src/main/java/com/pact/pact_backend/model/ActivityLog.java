package com.pact.pact_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String action; // e.g., "completed", "added", "joined"

    private String text; // e.g., Task title or context

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "activity_log_reactions", joinColumns = @JoinColumn(name = "activity_log_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "emoji")
    private java.util.Map<String, String> reactions = new java.util.HashMap<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
