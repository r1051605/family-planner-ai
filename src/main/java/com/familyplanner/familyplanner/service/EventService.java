package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.Event;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getBelgianHolidays() {
        int currentYear = LocalDate.now().getYear();
        List<Event> holidays = new ArrayList<>();

        // Create Belgian holidays for the current year
        createHoliday(holidays, "New Year's Day", currentYear, Month.JANUARY, 1);
        createHoliday(holidays, "Easter Monday", currentYear, Month.APRIL, 10); // Simplified - actual date varies
        createHoliday(holidays, "Labour Day", currentYear, Month.MAY, 1);
        createHoliday(holidays, "Ascension Day", currentYear, Month.MAY, 18); // Simplified - actual date varies
        createHoliday(holidays, "Whit Monday", currentYear, Month.MAY, 29); // Simplified - actual date varies
        createHoliday(holidays, "Belgian National Day", currentYear, Month.JULY, 21);
        createHoliday(holidays, "Assumption of Mary", currentYear, Month.AUGUST, 15);
        createHoliday(holidays, "All Saints' Day", currentYear, Month.NOVEMBER, 1);
        createHoliday(holidays, "Armistice Day", currentYear, Month.NOVEMBER, 11);
        createHoliday(holidays, "Christmas Day", currentYear, Month.DECEMBER, 25);

        return holidays;
    }

    private void createHoliday(List<Event> holidays, String name, int year, Month month, int day) {
        Event holiday = new Event();
        holiday.setTitle(name);
        holiday.setDescription("Belgian Public Holiday");
        holiday.setStartTime(LocalDateTime.of(year, month, day, 0, 0));
        holiday.setEndTime(LocalDateTime.of(year, month, day, 23, 59));
        holiday.setHoliday(true);

        holidays.add(holiday);
    }

    public List<Event> getEventsByFamily(Family family) {
        return eventRepository.findByFamily(family);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Map<String, Object>> getEventsForCalendar(LocalDateTime start, LocalDateTime end, User currentUser) {
        List<Map<String, Object>> events = new ArrayList<>();

        // Add Belgian holidays
        for (Event holiday : getBelgianHolidays()) {
            LocalDateTime holidayStart = holiday.getStartTime();
            LocalDateTime holidayEnd = holiday.getEndTime();

            if (!holidayStart.isAfter(end) && !holidayEnd.isBefore(start)) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("id", "holiday-" + holiday.getTitle().replaceAll("\\s+", "-").toLowerCase());
                eventMap.put("title", holiday.getTitle());
                eventMap.put("start", holiday.getStartTime().toString());
                eventMap.put("end", holiday.getEndTime().toString());
                eventMap.put("allDay", true);
                eventMap.put("className", "holiday-event");
                eventMap.put("editable", false);

                events.add(eventMap);
            }
        }

        // Add family events if user is logged in and has a family
        if (currentUser != null && currentUser.getFamily() != null) {
            List<Event> familyEvents = eventRepository.findByFamilyAndStartTimeBetween(
                    currentUser.getFamily(), start, end);

            for (Event event : familyEvents) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("id", event.getId());
                eventMap.put("title", event.getTitle());
                eventMap.put("start", event.getStartTime().toString());

                if (event.getEndTime() != null) {
                    eventMap.put("end", event.getEndTime().toString());
                }

                eventMap.put("description", event.getDescription());
                eventMap.put("editable", event.getCreatedBy().equals(currentUser)
                        || currentUser.getRole() == User.Role.PARENT);

                events.add(eventMap);
            }
        }

        return events;
    }

    public List<Event> getUpcomingEvents(Family family, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusMonths(1);

        return eventRepository.findByFamilyAndStartTimeBetweenOrderByStartTimeAsc(
                family, now, endDate, PageRequest.of(0, limit));
    }
}