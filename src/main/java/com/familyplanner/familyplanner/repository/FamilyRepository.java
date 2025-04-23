package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    // You can add custom query methods here if needed
}
