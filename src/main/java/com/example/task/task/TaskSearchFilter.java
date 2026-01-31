package com.example.task.task;

public record TaskSearchFilter(
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        TaskPriority priority,
        Integer pageSize,
        Integer pageNum
) {}
