# Pact

Collaborative accountability platform for small groups: join **rooms**, track **shared tasks**, **chat** in real time, and follow an **activity feed** when things get done.

This repository contains the **Pact backend** — a REST + WebSocket API built with **Spring Boot**, **JWT authentication**, and **MySQL**.

---

## Why Pact?

Roommates, study groups, and friend circles often agree on tasks but lose track in chats. Pact gives each group a dedicated space with assignable tasks, a clear status workflow, live updates, and room messaging so accountability stays visible.

---

## Features

- **User auth** — Register and login with JWT (stateless sessions)
- **Rooms** — Create rooms, share invite codes, join/leave, list members, delete rooms
- **Tasks** — Create, assign, update status, delete; statuses: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `VERIFIED`
- **Chat** — Room message history, send messages, emoji reactions
- **Activity feed** — Logged events (e.g. task added/completed) with reactions
- **Profiles** — View and update user profile fields
- **Real-time sync** — STOMP/WebSocket broadcasts for tasks, messages, and feed

---

## Tech stack

| Category | Tools |
|----------|--------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 (Web, Security, Data JPA, Validation, WebSocket) |
| Database | MySQL |
| Auth | JWT (jjwt) |
| Real-time | STOMP over SockJS |
| Build | Maven |

