package com.customers.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.customers.api.TasksApi;
import com.customers.db.model.VersionModel;
import com.customers.etag.CustomerETagResponseEntity;
import com.customers.model.CreateTasksRequestDTO;
import com.customers.model.ListTasksResponseDTO;
import com.customers.model.TaskDTO;
import com.customers.model.UpdateTaskRequestDTO;
import com.customers.service.TaskService;

import lombok.RequiredArgsConstructor;

/**
 * This is the controller layer to handle the client request for the task api.
 * It implements tasks Api interface which is generated already using open api generation.
 */
@RestController
@RequiredArgsConstructor
public class TaskController implements TasksApi {

	private final TaskService taskService;

	@Override
	public ResponseEntity<List<TaskDTO>> createTasks(Long customerId, CreateTasksRequestDTO createTasksRequestDTO) {
		List<TaskDTO> taskDTOS = taskService.createTasks(customerId, createTasksRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(taskDTOS);
	}

	@Override
	public ResponseEntity<Void> deleteTask(String taskId) {
		taskService.deleteTask(taskId);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> deleteTasks(Long customerId) {
		taskService.deleteTasks(customerId);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<TaskDTO> getTask(String taskId) {
		VersionModel<TaskDTO> taskDTOVersionModel = taskService.getTask(taskId);

		return new CustomerETagResponseEntity<>(taskDTOVersionModel).ok();
	}

	@Override
	public ResponseEntity<ListTasksResponseDTO> getTasks(Long customerId, Integer pageNumber, Integer pageSize, String sort) {
		ListTasksResponseDTO listTasksResponseDTO = taskService.getTasks(customerId,
				PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listTasksResponseDTO);
	}

	@Override
	public ResponseEntity<TaskDTO> updateTask(String taskId, String ifMatch, UpdateTaskRequestDTO updateTaskRequestDTO) {
		TaskDTO taskDTO = taskService.updateTask(taskId, ifMatch, updateTaskRequestDTO);
		return ResponseEntity.ok(taskDTO);
	}

}