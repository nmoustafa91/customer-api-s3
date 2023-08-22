package com.customers.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.customers.db.model.VersionModel;
import com.customers.model.CreateTasksRequestDTO;
import com.customers.model.ListTasksResponseDTO;
import com.customers.model.TaskDTO;
import com.customers.model.UpdateTaskRequestDTO;

public interface TaskService {

  VersionModel<TaskDTO> getTask(String taskId);

  List<TaskDTO> createTasks(Long customerId, CreateTasksRequestDTO createTasksRequestDTO);

  TaskDTO updateTask(String customerId, String ifMatch, UpdateTaskRequestDTO updateTaskRequestDTO);

  void deleteTask(String taskId);

  void deleteTasks(Long customerId);

  ListTasksResponseDTO getTasks(Long customerId, PageRequest pageRequest);

  }
