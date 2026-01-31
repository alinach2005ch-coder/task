package com.example.task;

import com.example.task.task.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskService taskService;
    private TaskEntity taskEntity;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        taskEntity= new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setCreatorId(100L);
        taskEntity.setAssignedUserId(200L);
        taskEntity.setCreateDate(LocalDate.now());
        taskEntity.setStatus(TaskStatus.CREATED);
        taskEntity.setDeadlineDate(LocalDate.now().plusDays(1));
        taskEntity.setPriority(TaskPriority.HIGH);

    }
    @Test
    void getTaskById_whenTaskExist(){
        when (taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        TaskResponse response =new TaskResponse(1L,100L,200L, TaskStatus.CREATED,
                LocalDate.now(), LocalDate.now().plusDays(1), TaskPriority.HIGH, null );
        when(taskMapper.toResponse(taskEntity)).thenReturn(response);
        TaskResponse result = taskService.getTaskById(1L);
        assertEquals(1L, result.id());
        assertEquals(TaskStatus.CREATED, result.status());
        verify(taskRepository).findById(1L);
    }
    @Test
    void getTask_whenTaskNotExist(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex =assertThrows(EntityNotFoundException.class, ()->taskService.getTaskById(1L));
        assertTrue(ex.getMessage().contains("Cannot find task with this id"));

    }
    @Test
    void createTask(){
        CreateTaskRequest request =new CreateTaskRequest(100L,200L,LocalDate.now().plusDays(1),
                TaskPriority.LOW );
        TaskEntity entityToSave= new TaskEntity();
        entityToSave.setCreatorId(100L);
        entityToSave.setAssignedUserId(200L);
        entityToSave.setDeadlineDate(LocalDate.now().plusDays(1));
        entityToSave.setPriority(TaskPriority.LOW);
        when(taskMapper.toEntity(request)).thenReturn(entityToSave);
        when(taskRepository.save(entityToSave)).thenReturn(entityToSave);
        TaskResponse response =new TaskResponse(1L,100L,200L, TaskStatus.CREATED,
                LocalDate.now(), LocalDate.now().plusDays(1), TaskPriority.LOW, null );
        when(taskMapper.toResponse(entityToSave)).thenReturn(response);
        TaskResponse result = taskService.createTask(request);
        assertEquals(TaskStatus.CREATED, result.status());
        assertEquals(200L, result.assignedUserId());
        verify(taskRepository).save(entityToSave);
    }
    @Test
    void updateTask_whenDone(){
        taskEntity.setStatus(TaskStatus.DONE);
        UpdateTaskRequest updateTaskRequest= new UpdateTaskRequest(500L,LocalDate.now().plusDays(5),
                TaskPriority.HIGH);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalStateException ex = assertThrows(IllegalStateException.class,()->taskService.updateTask(1L,updateTaskRequest));
        assertEquals("You cannot update this task", ex.getMessage());

    }

    @Test
    void taskInProgress_whenNoAssignedUserAndDeadline(){
        taskEntity.setAssignedUserId(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalStateException ex = assertThrows(IllegalStateException.class,()->taskService.inProgressStatus(1L));
        assertEquals("You need assigned user", ex.getMessage());
    }

    @Test
    void inProgress_whenDoneAndDeadlinePassed(){
        taskEntity.setStatus(TaskStatus.DONE);
        taskEntity.setDeadlineDate(LocalDate.now().minusDays(1));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> taskService.inProgressStatus(1L));
        assertEquals("You cannot change task",ex.getMessage());
    }

    @Test
    void inProgress_whenMax4TasksInProgress(){
        taskEntity.setAssignedUserId(200L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(200L, TaskStatus.IN_PROGRESS)).thenReturn(4L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> taskService.inProgressStatus(1L));
        assertEquals("You can't have more than 4 tasks in progress", ex.getMessage());
    }
    @Test
    void inProgressTask(){
        taskEntity.setAssignedUserId(200L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(200L,TaskStatus.IN_PROGRESS)).thenReturn(2L);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        TaskResponse response = new TaskResponse(1L,100L,200L,TaskStatus.IN_PROGRESS,LocalDate.now(),LocalDate.now().plusDays(1), TaskPriority.HIGH,null);
        when(taskMapper.toResponse(taskEntity)).thenReturn(response);
        TaskResponse result = taskService.inProgressStatus(1L);
        assertEquals(TaskStatus.IN_PROGRESS, result.status());
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void doneStatus(){
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        TaskResponse response = new TaskResponse(1L, 100L, 200L, TaskStatus.DONE,
                LocalDate.now(), taskEntity.getDeadlineDate(), TaskPriority.HIGH, LocalDateTime.now());
        when(taskMapper.toResponse(taskEntity)).thenReturn(response);
        TaskResponse result = taskService.doneStatus(1L);
        assertEquals(TaskStatus.DONE, result.status());
        assertNotNull(result.doneDateTime());
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void doneStatus_whenNoDeadline(){
        taskEntity.setDeadlineDate(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> taskService.doneStatus(1L));
        assertEquals("You need deadline date!", ex.getMessage());
    }
    @Test
    void doneStatus_whenNoAssignedUser(){
        taskEntity.setAssignedUserId(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()-> taskService.doneStatus(1L));
        assertEquals("You need assigned user!", ex.getMessage());
    }
    @Test
    void doneStatus_whenTaskDone(){
        taskEntity.setStatus(TaskStatus.DONE);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()-> taskService.doneStatus(1L));
        assertEquals("Task is done", ex.getMessage());
    }

}
