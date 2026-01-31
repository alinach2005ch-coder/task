package com.example.task.task;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        LocalDate createDate,
        LocalDate deadlineDate,
        TaskPriority priority,
        LocalDateTime doneDateTime
) {
}


