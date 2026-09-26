package com.pact.pact_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String handle;
    private String avatar;
    private String status;
    private String bio;
    private String location;
    private String insta;
    private String spotify;

    private LocalDateTime createdAt = LocalDateTime.now(Clock.systemUTC());
}