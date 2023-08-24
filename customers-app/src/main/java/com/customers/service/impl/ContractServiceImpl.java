package com.customers.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customers.db.model.Customer;
import com.customers.db.model.Contract;
import com.customers.db.model.VersionModel;
import com.customers.db.repository.CustomerRepository;
import com.customers.db.repository.ContractRepository;
import com.customers.etag.utils.ETagUtils;
import com.customers.exception.NotFoundException;
import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ErrorCode;
import com.customers.mapper.ContractMapper;
import com.customers.model.CreateContractsRequestDTO;
import com.customers.model.ListContractsResponseDTO;
import com.customers.model.ContractDTO;
import com.customers.model.UpdateContractRequestDTO;
import com.customers.service.ContractService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

  private final ContractRepository contractRepository;
  private final CustomerRepository customerRepository;
  private final ContractMapper contractMapper;

  @Override
  @Transactional
  public VersionModel<ContractDTO> getContract(String contractId) {
    Contract contract = getContractById(contractId);

    return contractMapper.entityToVersionModel(contract);
  }

  @Override
  public List<ContractDTO> createContracts(Long customerId, CreateContractsRequestDTO createContractsRequestDTO) {
    Customer customer = getCustomerById(customerId);
    List<ContractDTO> results = new ArrayList<>();
    createContractsRequestDTO.getContracts().forEach(currentContract -> {
      Contract contract = contractMapper.toContractEntity(currentContract);
      contract.setCustomer(customer);
      results.add(contractMapper.fromEntity(contractRepository.save(contract)));
    });
    return results;
  }

  @Override
  @Transactional
  public ContractDTO updateContract(String contractId, String ifMatch, UpdateContractRequestDTO updateContractRequestDTO) {
    Contract contract = getContractById(contractId);

    ETagUtils.checkETag(contract, ifMatch);

    contract = contractMapper.updateEntityFromModel(updateContractRequestDTO, contract);

    return contractMapper.fromEntity(contractRepository.save(contract));
  }

  @Override
  public void deleteContract(String contractId) {
    Contract contract = getContractById(contractId);
    contractRepository.delete(contract);
  }

  @Override
  @Transactional
  public void deleteContracts(Long customerId) {
    contractRepository.deleteAllByCustomerCustomerId(customerId);
  }

  @Override
  public ListContractsResponseDTO getContracts(Long customerId, PageRequest pageRequest) {
    Page<Contract> page = contractRepository.findAllByCustomerCustomerId(customerId, pageRequest);
    return contractMapper.pageToContractsResponseDTO(page);
  }

  private Customer getCustomerById(Long customerId) {
    return customerRepository.findById(customerId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(customerId))
            .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND)));
  }

  private Contract getContractById(String contractId) {
    return contractRepository.findById(contractId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(contractId))
            .setCodeAndMessage(ErrorCode.CONTRACT_NOT_FOUND)));
  }
}
