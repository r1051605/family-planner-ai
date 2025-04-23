package com.familyplanner.familyplanner.service;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.model.UserStatus;
import com.familyplanner.familyplanner.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StatusService {

    private final UserStatusRepository userStatusRepository;

    @Autowired
    public StatusService(UserStatusRepository userStatusRepository) {
        this.userStatusRepository = userStatusRepository;
    }

    public UserStatus getUserStatus(User user) {
        return userStatusRepository.findByUser(user).orElse(null);
    }

    public Map<Long, String> getFamilyMemberStatuses(Family family) {
        List<UserStatus> statuses = userStatusRepository.findByUser_Family(family);
        Map<Long, String> statusMap = new HashMap<>();

        for (UserStatus status : statuses) {
            statusMap.put(status.getUser().getId(), status.getStatus().toString());
        }

        return statusMap;
    }

    public void updateUserStatus(User user, UserStatus.Status status) {
        Optional<UserStatus> existingStatus = userStatusRepository.findByUser(user);

        if (existingStatus.isPresent()) {
            UserStatus userStatus = existingStatus.get();
            userStatus.setStatus(status);
            userStatus.setLastUpdated(LocalDateTime.now());
            userStatusRepository.save(userStatus);
        } else {
            UserStatus userStatus = new UserStatus();
            userStatus.setUser(user);
            userStatus.setStatus(status);
            userStatus.setLastUpdated(LocalDateTime.now());
            userStatusRepository.save(userStatus);
        }
    }

    // Set default status for a new user
    public void initializeUserStatus(User user) {
        UserStatus userStatus = new UserStatus();
        userStatus.setUser(user);
        userStatus.setStatus(UserStatus.Status.HOME);
        userStatus.setLastUpdated(LocalDateTime.now());

        userStatusRepository.save(userStatus);
    }
}