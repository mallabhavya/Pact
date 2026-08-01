package com.pact.pact_backend.repository;
import com.pact.pact_backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByRoomId(Long roomId);
}