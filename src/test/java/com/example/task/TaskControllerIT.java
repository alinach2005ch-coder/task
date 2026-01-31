package com.example.task;

import com.example.task.task.CreateTaskRequest;
import com.example.task.task.TaskPriority;
import com.example.task.task.TaskResponse;
import com.example.task.task.UpdateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void createTask_returnCreated() throws Exception{
        CreateTaskRequest request= new CreateTaskRequest(1L,2L, LocalDate.now().plusDays(1), TaskPriority.HIGH);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.creatorId").value(1))
                .andExpect(jsonPath("$.assignedUserId").value(2))
                .andExpect(jsonPath("$.status").value("CREATED"));


    }

    @Test
    void createTask_returnBadRequest_deadlineInPast() throws Exception{
        CreateTaskRequest request = new CreateTaskRequest(1L,2L, LocalDate.now().minusDays(1), TaskPriority.HIGH);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getAllTasks_returnOk() throws Exception{
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void getTaskById_returnNotFound() throws Exception{
        mockMvc.perform(get("/tasks/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"));
    }
    @Test
    void updateTask_whenDone() throws Exception{
        CreateTaskRequest createTaskRequest = new CreateTaskRequest(1L, 2L, LocalDate.now().plusDays(1), TaskPriority.HIGH);
        String json =mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andReturn().getResponse().getContentAsString();
        TaskResponse created = objectMapper.readValue(json, TaskResponse.class);
        mockMvc.perform(post("/tasks/"+created.id()+"/complete"))
                .andExpect(status().isOk());
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(3L, LocalDate.now().plusDays(5), TaskPriority.LOW);
        mockMvc.perform(put("/tasks/"+ created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTaskRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request"));

    }
}
