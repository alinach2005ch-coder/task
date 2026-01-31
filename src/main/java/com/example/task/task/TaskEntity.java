package com.example.task.task;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
public class TaskEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    @Column(name = "assigned_user_id")
    private Long assignedUserId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;
    @Column(name = "deadline_date")
    private LocalDate deadlineDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;
    @Column(name="done_date_time" )

    private LocalDateTime doneDateTime;
    public TaskPriority getPriority() {
        return priority;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getId() {
        return id;
    }

    public void setDoneDateTime(LocalDateTime doneDateTime) {
        this.doneDateTime = doneDateTime;
    }

    public LocalDateTime getDoneDateTime() {
        return doneDateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setCreateDate(LocalDate createData) {
        this.createDate = createData;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskEntity(Long id, Long creatorId, Long assignedUserId, TaskStatus status, LocalDate createDate, LocalDate deadlineDate, TaskPriority priority, LocalDateTime doneDateTime) {
        this.id = id;
        this.creatorId = creatorId;
        this.assignedUserId = assignedUserId;
        this.status = status;
        this.createDate = createDate;
        this.deadlineDate = deadlineDate;
        this.priority = priority;
        this.doneDateTime = doneDateTime;
    }

    public TaskEntity() {

    }
}
