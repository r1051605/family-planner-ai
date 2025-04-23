package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByFamily(Family family);
}
