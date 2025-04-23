package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByFamily(Family family);
    boolean existsByUsername(String username);
}