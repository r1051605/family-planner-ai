package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.ChatMessage;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.ChatService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService, ThemeService themeService) {
        this.chatService = chatService;
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping
    public String viewChat(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        // Get chat messages for the current family
        List<ChatMessage> messages = chatService.getRecentMessages(currentUser.getFamily(), 50);
        model.addAttribute("messages", messages);

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "chat/chat";
    }

    @MessageMapping("/send")
    @SendTo("/topic/family")
    public ChatMessage sendMessage(ChatMessage message, Authentication authentication) {
        User sender = userService.findByUsername(authentication.getName());

        message.setSender(sender);
        message.setFamily(sender.getFamily());
        message.setTimestamp(LocalDateTime.now());

        return chatService.saveMessage(message);
    }
}