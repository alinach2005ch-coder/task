package com.example.task.task;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RequestMapping("/tasks")
@RestController
public class TaskController {
    private static final Logger log =
            LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping()
    public ResponseEntity <List<TaskResponse>> getAllTasks (
            @RequestParam(name ="creatorId", required = false) Long creatorId,
            @RequestParam(name ="assignedUserId", required = false) Long assignedUserId,
            @RequestParam(name ="status",required = false) TaskStatus status,
            @RequestParam(name ="priority", required = false) TaskPriority priority,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name= "pageNum",required = false) Integer pageNum

    ){
        log.info("Called getAllTasks");
        var filter = new TaskSearchFilter(
                creatorId,
                assignedUserId,
                status,
                priority,
                pageSize,
                pageNum
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.findAllTasks(filter));
    }


    @GetMapping("/{id}")
    public ResponseEntity <TaskResponse> getTaskById (
            @PathVariable ("id") Long id)
    {
        log.info("Called getTaskById id={}",id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.getTaskById(id));

    }
    @PostMapping()
    public ResponseEntity <TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest taskToCreate
    ){
        log.info("Called create task");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskToCreate)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity< TaskResponse >updateTask(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateTaskRequest taskToUpdate) {
        log.info("Called update task id={}",id);
        var updated = taskService.updateTask(id,taskToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteTask(
            @PathVariable("id") Long id
    ){
        log.info("Called delete task with id={} ",id);
        taskService.deleteTask(id);
        return ResponseEntity.ok()
                .build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TaskResponse> inProgressStatus(
            @PathVariable ("id") Long id
    ){
        log.info("Called start task with id={} ",id);

        return ResponseEntity.ok(taskService.inProgressStatus(id));

    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> doneStatus(
            @PathVariable("id") Long id
    ) {
        log.info("Task completed with id={}", id);
        return ResponseEntity.ok(taskService.doneStatus(id));
    }
}
