package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.Task;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksByFamily(Family family) {
        return taskRepository.findByFamily(family);
    }

    public List<Task> getTasksByAssignee(User assignee) {
        return taskRepository.findByAssignedTo(assignee);
    }

    public List<Task> getUnassignedTasks(Family family) {
        return taskRepository.findByFamilyAndAssignedToIsNull(family);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Method to initialize default tasks for a new family
    public void initializeDefaultTasks(Family family, User creator) {
        // Create standard household tasks
        createDefaultTask("Clean the kitchen", "Daily kitchen cleaning", family, creator);
        createDefaultTask("Take out the trash", "Empty all trash bins", family, creator);
        createDefaultTask("Do the laundry", "Wash, dry, and fold clothes", family, creator);
        createDefaultTask("Vacuum the living room", "Weekly vacuum cleaning", family, creator);
        createDefaultTask("Clean the bathroom", "Clean toilet, sink, and shower", family, creator);
        createDefaultTask("Go grocery shopping", "Buy groceries for the week", family, creator);
        createDefaultTask("Water the plants", "Water all house plants", family, creator);
        createDefaultTask("Mow the lawn", "Mow the front and back lawn", family, creator);
    }

    private void createDefaultTask(String title, String description, Family family, User creator) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setFamily(family);
        task.setCreatedBy(creator);
        task.setStatus(Task.TaskStatus.PENDING);

        taskRepository.save(task);
    }
}