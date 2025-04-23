package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.Task;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.TaskService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, ThemeService themeService) {
        this.taskService = taskService;
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping
    public String viewTasks(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        // Get tasks for the current family
        List<Task> familyTasks = taskService.getTasksByFamily(currentUser.getFamily());
        model.addAttribute("familyTasks", familyTasks);

        // Get tasks assigned to current user
        List<Task> userTasks = taskService.getTasksByAssignee(currentUser);
        model.addAttribute("userTasks", userTasks);

        // Get unassigned tasks
        List<Task> unassignedTasks = taskService.getUnassignedTasks(currentUser.getFamily());
        model.addAttribute("unassignedTasks", unassignedTasks);

        // Add a new task model for the form
        model.addAttribute("newTask", new Task());

        // Add family members for task assignment
        model.addAttribute("familyMembers", userService.getFamilyMembers(currentUser.getFamily()));

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "tasks/tasks";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute("newTask") Task task,
                             BindingResult result,
                             Authentication authentication) {

        if (result.hasErrors()) {
            return "tasks/tasks";
        }

        User currentUser = userService.findByUsername(authentication.getName());

        // Check if user has permission to create task
        if (currentUser.getRole() == User.Role.KID) {
            return "redirect:/tasks?error=nopermission";
        }

        task.setCreatedBy(currentUser);
        task.setFamily(currentUser.getFamily());
        task.setStatus(Task.TaskStatus.PENDING);

        taskService.saveTask(task);

        return "redirect:/tasks";
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<Void> claimTask(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Task task = taskService.getTaskById(id);

        // Check if task belongs to user's family and is unassigned
        if (task.getFamily().equals(currentUser.getFamily()) && task.getAssignedTo() == null) {
            task.setAssignedTo(currentUser);
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            taskService.saveTask(task);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Task task = taskService.getTaskById(id);

        // Check if task belongs to user's family and is assigned to current user
        if (task.getFamily().equals(currentUser.getFamily()) &&
                (task.getAssignedTo() != null && task.getAssignedTo().equals(currentUser))) {

            task.setStatus(Task.TaskStatus.COMPLETED);
            taskService.saveTask(task);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Task task = taskService.getTaskById(id);

        // Check if user has permission to delete
        if (task.getFamily().equals(currentUser.getFamily()) &&
                (task.getCreatedBy().equals(currentUser) || currentUser.getRole() == User.Role.PARENT)) {

            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<Void> assignTask(@PathVariable Long id,
                                           @PathVariable Long userId,
                                           Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Task task = taskService.getTaskById(id);
        User assignee = userService.findById(userId);

        // Check if user has permission to assign tasks
        if (currentUser.getRole() == User.Role.KID) {
            return ResponseEntity.badRequest().build();
        }

        // Check if task and assignee belong to user's family
        if (task.getFamily().equals(currentUser.getFamily()) &&
                assignee.getFamily().equals(currentUser.getFamily())) {

            task.setAssignedTo(assignee);
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            taskService.saveTask(task);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}