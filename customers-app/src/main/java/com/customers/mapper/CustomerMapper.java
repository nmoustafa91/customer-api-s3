package com.customers.mapper;

import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import com.customers.db.model.Customer;
import com.customers.db.model.VersionModel;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.PagingDTO;
import com.customers.model.UpdateCustomerRequestDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

  CustomerDTO fromEntity(Customer entity);

  Customer fromCreateBodyToEntity(CreateCustomerRequestDTO createCustomerRequestDTO);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Customer updateEntityFromModel(UpdateCustomerRequestDTO dto,
      @MappingTarget Customer entity);

  default VersionModel<CustomerDTO> entityToVersionModel(Customer entity) {
    return new VersionModel<>(entity.getVersion(),
        fromEntity(entity));
  }

  default ListCustomersResponseDTO pageToCustomersResponseDTO(Page<Customer> page) {
    return new ListCustomersResponseDTO()
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
