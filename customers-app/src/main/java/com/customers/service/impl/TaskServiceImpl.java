package com.customers.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customers.db.model.Customer;
import com.customers.db.model.Task;
import com.customers.db.model.VersionModel;
import com.customers.db.repository.CustomerRepository;
import com.customers.db.repository.TaskRepository;
import com.customers.etag.utils.ETagUtils;
import com.customers.exception.NotFoundException;
import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ErrorCode;
import com.customers.mapper.TaskMapper;
import com.customers.model.CreateTasksRequestDTO;
import com.customers.model.ListTasksResponseDTO;
import com.customers.model.TaskDTO;
import com.customers.model.UpdateTaskRequestDTO;
import com.customers.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final CustomerRepository customerRepository;
  private final TaskMapper taskMapper;

  @Override
  @Transactional
  public VersionModel<TaskDTO> getTask(String taskId) {
    Task task = getTaskById(taskId);

    return taskMapper.entityToVersionModel(task);
  }

  @Override
  public List<TaskDTO> createTasks(Long customerId, CreateTasksRequestDTO createTasksRequestDTO) {
    Customer customer = getCustomerById(customerId);
    List<TaskDTO> results = new ArrayList<>();
    createTasksRequestDTO.getTasks().forEach(currentTask -> {
      Task task = taskMapper.toTaskEntity(currentTask);
      task.setCustomer(customer);
      results.add(taskMapper.fromEntity(taskRepository.save(task)));
    });
    return results;
  }

  @Override
  @Transactional
  public TaskDTO updateTask(String taskId, String ifMatch, UpdateTaskRequestDTO updateTaskRequestDTO) {
    Task task = getTaskById(taskId);

    ETagUtils.checkETag(task, ifMatch);

    task = taskMapper.updateEntityFromModel(updateTaskRequestDTO, task);

    return taskMapper.fromEntity(taskRepository.save(task));
  }

  @Override
  public void deleteTask(String taskId) {
    Task task = getTaskById(taskId);
    taskRepository.delete(task);
  }

  @Override
  @Transactional
  public void deleteTasks(Long customerId) {
    taskRepository.deleteAllByCustomerCustomerId(customerId);
  }

  @Override
  public ListTasksResponseDTO getTasks(Long customerId, PageRequest pageRequest) {
    Page<Task> page = taskRepository.findAllByCustomerCustomerId(customerId, pageRequest);
    return taskMapper.pageToTasksResponseDTO(page);
  }

  private Customer getCustomerById(Long customerId) {
    return customerRepository.findById(customerId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(customerId))
            .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND)));
  }

  private Task getTaskById(String taskId) {
    return taskRepository.findById(taskId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(taskId))
            .setCodeAndMessage(ErrorCode.TASK_NOT_FOUND)));
  }
}
