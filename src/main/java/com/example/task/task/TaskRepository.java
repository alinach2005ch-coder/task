package com.example.task.task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    long countByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus attr0);


    @Query("""

            SELECT t from TaskEntity t
            where (:creatorId IS NULL OR t.creatorId = :creatorId)
            and(:assignedUserId IS NULL OR t.assignedUserId = :assignedUserId)
            and(:status IS NULL OR t.status = :status )
            and(:priority IS NULL OR t.priority = :priority)

""")
    List<TaskEntity> searchByFilter(
            @Param("creatorId") Long creatorId,
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            Pageable pageable
    );

}