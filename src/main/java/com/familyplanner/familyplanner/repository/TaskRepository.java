package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.Task;
import com.familyplanner.familyplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByFamily(Family family);
    List<Task> findByAssignedTo(User assignee);
    List<Task> findByFamilyAndAssignedToIsNull(Family family);
}