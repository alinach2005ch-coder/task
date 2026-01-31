package com.example.task.task;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(
            TaskEntity taskEntity
    ){
        return new TaskResponse(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getCreateDate(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                taskEntity.getDoneDateTime()
        );
    }
    public TaskEntity toEntity(
            CreateTaskRequest taskToCreate
    )
    {
        TaskEntity entity=new TaskEntity();
        entity.setCreatorId(taskToCreate.creatorId());
        entity.setAssignedUserId(taskToCreate.assignedUserId());
        entity.setDeadlineDate(taskToCreate.deadlineDate());
        entity.setPriority(taskToCreate.priority());
        return entity;
    }
}
