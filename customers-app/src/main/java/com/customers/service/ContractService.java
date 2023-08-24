package com.customers.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.customers.db.model.VersionModel;
import com.customers.model.CreateContractsRequestDTO;
import com.customers.model.ListContractsResponseDTO;
import com.customers.model.ContractDTO;
import com.customers.model.UpdateContractRequestDTO;

public interface ContractService {

  VersionModel<ContractDTO> getContract(String contractId);

  List<ContractDTO> createContracts(Long customerId, CreateContractsRequestDTO createContractsRequestDTO);

  ContractDTO updateContract(String customerId, String ifMatch, UpdateContractRequestDTO updateContractRequestDTO);

  void deleteContract(String contractId);

  void deleteContracts(Long customerId);

  ListContractsResponseDTO getContracts(Long customerId, PageRequest pageRequest);

  }
