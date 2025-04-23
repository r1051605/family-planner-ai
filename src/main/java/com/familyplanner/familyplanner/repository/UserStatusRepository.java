package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByUser(User user);
    List<UserStatus> findByUser_Family(Family family);
}
