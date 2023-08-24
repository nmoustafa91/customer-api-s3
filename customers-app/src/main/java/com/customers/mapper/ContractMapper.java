package com.customers.mapper;

import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.customers.db.model.Contract;
import com.customers.db.model.VersionModel;
import com.customers.model.ListContractsResponseDTO;
import com.customers.model.PagingDTO;
import com.customers.model.ContractDTO;
import com.customers.model.ContractDetailsDTO;
import com.customers.model.UpdateContractRequestDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContractMapper {

  @Mapping(target = "customerId", source = "customer.customerId")
  ContractDTO fromEntity(Contract contract);

  Contract toContractEntity(ContractDetailsDTO contractDTO);

  Contract updateEntityFromModel(UpdateContractRequestDTO updateContractRequestDTO, @MappingTarget Contract contract);

  default VersionModel<ContractDTO> entityToVersionModel(Contract entity) {
    return new VersionModel<>(entity.getVersion(),
        fromEntity(entity));
  }

  default ListContractsResponseDTO pageToContractsResponseDTO(Page<Contract> page) {
    return new ListContractsResponseDTO()
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
