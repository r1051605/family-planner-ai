package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.Group;
import com.familyplanner.familyplanner.model.GroupMember;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.repository.GroupMemberRepository;
import com.familyplanner.familyplanner.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public List<Group> getGroupsByFamily(Family family) {
        return groupRepository.findByFamily(family);
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public GroupMember getGroupMemberById(Long id) {
        return groupMemberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Group member not found"));
    }

    public GroupMember saveGroupMember(GroupMember member) {
        return groupMemberRepository.save(member);
    }

    public void deleteGroupMember(Long id) {
        groupMemberRepository.deleteById(id);
    }

    public List<GroupMember> getUpcomingVisits(Family family) {
        LocalDateTime now = LocalDateTime.now();
        return groupMemberRepository.findByGroup_FamilyAndVisitDateGreaterThanOrderByVisitDateAsc(family, now);
    }

    // Method to initialize default groups for a new family
    public void initializeDefaultGroups(Family family, User creator) {
        // Create a few example groups
        createDefaultGroup("Close Friends", "Friends who visit regularly", family, creator);
        createDefaultGroup("Extended Family", "Relatives who visit occasionally", family, creator);
        createDefaultGroup("Kids' Friends", "Friends of the children", family, creator);
    }

    private void createDefaultGroup(String name, String description, Family family, User creator) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setFamily(family);
        group.setCreatedBy(creator);

        groupRepository.save(group);
    }
}