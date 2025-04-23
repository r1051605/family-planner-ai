package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.ChatMessage;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessage> getRecentMessages(Family family, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findByFamily(
                family,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"))
        );

        // Reverse to show oldest messages first
        Collections.reverse(messages);

        return messages;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}