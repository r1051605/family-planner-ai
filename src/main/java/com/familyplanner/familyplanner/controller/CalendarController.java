package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.Event;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.EventService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final EventService eventService;
    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public CalendarController(EventService eventService, UserService userService, ThemeService themeService) {
        this.eventService = eventService;
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping("/view")
    public String viewCalendar(Model model, Authentication authentication) {
        User currentUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("user", currentUser);

            // Add family events if user is logged in
            if (currentUser.getFamily() != null) {
                model.addAttribute("familyEvents", eventService.getEventsByFamily(currentUser.getFamily()));
            }
        }

        // Add Belgian holidays for everyone
        model.addAttribute("holidays", eventService.getBelgianHolidays());
        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "calendar/view";
    }

    @GetMapping
    public String calendar(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        // Add family events
        if (currentUser.getFamily() != null) {
            model.addAttribute("familyEvents", eventService.getEventsByFamily(currentUser.getFamily()));
        }

        // Add Belgian holidays
        model.addAttribute("holidays", eventService.getBelgianHolidays());
        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        // Add new event form
        model.addAttribute("newEvent", new Event());

        return "calendar/calendar";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication authentication) {

        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userService.findByUsername(authentication.getName());
        }

        return eventService.getEventsForCalendar(start, end, currentUser);
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute("newEvent") Event event,
                              BindingResult result,
                              Authentication authentication) {

        if (result.hasErrors()) {
            return "calendar/calendar";
        }

        User currentUser = userService.findByUsername(authentication.getName());
        event.setCreatedBy(currentUser);
        event.setFamily(currentUser.getFamily());

        eventService.saveEvent(event);

        return "redirect:/calendar";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Event event = eventService.getEventById(id);

        // Check if user has permission to delete
        if (event.getFamily().equals(currentUser.getFamily()) &&
                (event.getCreatedBy().equals(currentUser) || currentUser.getRole() == User.Role.PARENT)) {

            eventService.deleteEvent(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/sync")
    public String syncCalendars(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "calendar/sync";
    }
}