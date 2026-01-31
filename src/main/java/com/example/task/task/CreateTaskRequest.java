package com.example.task.task;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTaskRequest (
        @NotNull
        Long creatorId,
        @NotNull
        Long  assignedUserId,
        @NotNull
        @FutureOrPresent
        LocalDate deadlineDate,
        @NotNull
        TaskPriority priority

){
}
