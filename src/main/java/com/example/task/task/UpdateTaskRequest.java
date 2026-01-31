package com.example.task.task;

import java.time.LocalDate;

public record UpdateTaskRequest(
        Long assignedUserId,
        LocalDate deadlineDate,
        TaskPriority priority
) {
}
