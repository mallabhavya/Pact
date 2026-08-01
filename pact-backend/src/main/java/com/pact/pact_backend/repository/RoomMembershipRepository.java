package com.pact.pact_backend.repository;
import com.pact.pact_backend.model.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    List<RoomMembership> findByUserId(Long userId);
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
}