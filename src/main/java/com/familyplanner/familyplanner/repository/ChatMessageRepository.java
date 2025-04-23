package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.ChatMessage;
import com.familyplanner.familyplanner.model.Family;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByFamily(Family family, Pageable pageable);
}