package com.pact.pact_backend.repository;

import com.pact.pact_backend.model.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    List<RoomMembership> findByRoomId(Long roomId);
    List<RoomMembership> findByUserId(Long userId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}