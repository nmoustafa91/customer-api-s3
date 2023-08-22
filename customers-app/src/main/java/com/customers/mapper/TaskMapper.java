package com.customers.mapper;

import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.customers.db.model.Task;
import com.customers.db.model.VersionModel;
import com.customers.model.ListTasksResponseDTO;
import com.customers.model.PagingDTO;
import com.customers.model.TaskDTO;
import com.customers.model.TaskDetailsDTO;
import com.customers.model.UpdateTaskRequestDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TaskMapper {

  @Mapping(target = "customerId", source = "customer.customerId")
  TaskDTO fromEntity(Task task);

  Task toTaskEntity(TaskDetailsDTO taskDTO);

  Task updateEntityFromModel(UpdateTaskRequestDTO updateTaskRequestDTO, @MappingTarget Task task);

  default VersionModel<TaskDTO> entityToVersionModel(Task entity) {
    return new VersionModel<>(entity.getVersion(),
        fromEntity(entity));
  }

  default ListTasksResponseDTO pageToTasksResponseDTO(Page<Task> page) {
    return new ListTasksResponseDTO()
        .results(page.get().map(this::fromEntity).collect(Collectors.toList()))
        .paging(createPagingResponseFromPage(page));
  }

  default PagingDTO createPagingResponseFromPage(Page<?> page) {
    return new PagingDTO()
        .pageNumber(page.getNumber())
        .pageSize(page.getSize())
        .pageCount(page.getTotalPages())
        .totalElements(page.getTotalElements());
  }
}
