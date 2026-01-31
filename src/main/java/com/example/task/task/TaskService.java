package com.example.task.task;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static com.example.task.task.TaskStatus.*;


@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    public TaskService(TaskMapper taskMapper, TaskRepository taskRepository) {
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;





    }

    public TaskResponse getTaskById (Long id){

        TaskEntity taskEntity= taskRepository.findById(id)
                .orElseThrow(()->  new EntityNotFoundException ( "Cannot find task with this id"+ id));

        return taskMapper.toResponse(taskEntity);



    }

    public List<TaskResponse> findAllTasks(
            TaskSearchFilter filter
    ){
        int pageSize = filter.pageSize() != null
                ? filter.pageSize() : 10;
        int pageNum= filter.pageNum() != null
                ? filter.pageNum() : 0;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNum);
        List<TaskEntity> allEntities =taskRepository.searchByFilter(
                filter.creatorId(),
                filter.assignedUserId(),
                filter.status(),
                filter.priority(),
                pageable
        );
        return  allEntities.stream().map(taskMapper::toResponse).toList();

    }


    public TaskResponse createTask(CreateTaskRequest taskToCreate) {


        var entityToSave = taskMapper.toEntity(taskToCreate);
        entityToSave.setStatus(CREATED);
        entityToSave.setCreateDate(LocalDate.now());

        var entitySave =  taskRepository.save(entityToSave);
        return taskMapper.toResponse(entitySave);

    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest taskToUpdate) {


        var taskEntity =taskRepository.findById(id)
                .orElseThrow(()->  new EntityNotFoundException ( "Cannot find task with this id"+ id));
        if (taskEntity.getStatus()==TaskStatus.DONE) {
            throw new IllegalStateException("You cannot update this task");
        }

        if(taskToUpdate.assignedUserId()!= null){
            taskEntity.setAssignedUserId(taskToUpdate.assignedUserId());
        }
        if(taskToUpdate.deadlineDate()!=null){
            taskEntity.setDeadlineDate(taskToUpdate.deadlineDate());
        }
        if(taskToUpdate.priority() != null){
            taskEntity.setPriority(taskToUpdate.priority());
        }
        var entityUpdate = taskRepository.save(taskEntity);
        return taskMapper.toResponse(entityUpdate);
    }

    public void deleteTask(Long id) {
        if(!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot find this id");
        }
        taskRepository.deleteById(id);
    }


    public  TaskResponse inProgressStatus(Long id) {

        var taskEntity = taskRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Cannot find el with this id"));
        if((taskEntity.getStatus()== DONE) ){
            if(taskEntity.getDeadlineDate()==null){
                throw new IllegalStateException("Task must have deadline");
            }
            if( taskEntity.getDeadlineDate().isBefore(LocalDate.now())){
                throw new IllegalArgumentException("You cannot change task");

            }
        }
        if(taskEntity.getAssignedUserId()==null){
            throw new IllegalStateException("You need assigned user");
        }


        long activeTask = taskRepository.countByAssignedUserIdAndStatus(
                taskEntity.getAssignedUserId(),
                IN_PROGRESS
        );
        if(activeTask>=4){
            throw new IllegalArgumentException("You can't have more than 4 tasks in progress");
        }
        taskEntity.setStatus(IN_PROGRESS);
        taskRepository.save(taskEntity);
        return taskMapper.toResponse(taskEntity);

    }

    public  TaskResponse doneStatus(Long id) {

        var taskEntity = taskRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Cannot find el with this id"));


        if(taskEntity.getAssignedUserId()== null){
            throw new IllegalStateException("You need assigned user!");
        }
        if(taskEntity.getDeadlineDate()== null){
            throw new IllegalArgumentException("You need deadline date!");
        }
        if(taskEntity.getStatus()==DONE){
            throw new IllegalStateException("Task is done");
        }
        taskEntity.setStatus(DONE);
        taskEntity.setDoneDateTime(LocalDateTime.now());
        taskRepository.save(taskEntity);
        return taskMapper.toResponse(taskEntity);
    }
}
