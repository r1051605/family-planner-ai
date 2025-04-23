package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Event;
import com.familyplanner.familyplanner.model.Family;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByFamily(Family family);
    List<Event> findByFamilyAndStartTimeBetween(Family family, LocalDateTime start, LocalDateTime end);
    List<Event> findByFamilyAndStartTimeBetweenOrderByStartTimeAsc(Family family, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
