package com.customers.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.customers.api.ContractsApi;
import com.customers.db.model.VersionModel;
import com.customers.etag.CustomerETagResponseEntity;
import com.customers.model.CreateContractsRequestDTO;
import com.customers.model.ListContractsResponseDTO;
import com.customers.model.ContractDTO;
import com.customers.model.UpdateContractRequestDTO;
import com.customers.service.ContractService;

import lombok.RequiredArgsConstructor;

/**
 * This is the controller layer to handle the client request for the contract api.
 * It implements contracts Api interface which is generated already using open api generation.
 */
@RestController
@RequiredArgsConstructor
public class ContractController implements ContractsApi {

	private final ContractService contractService;

	/**
	 *
	 * An API to assign contracts to a specific customer, it uses bulk creation.
	 *
	 * @param customerId  (required)
	 * @param createContractsRequestDTO  (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<List<ContractDTO>> createContracts(Long customerId, CreateContractsRequestDTO createContractsRequestDTO) {
		List<ContractDTO> contractDTOS = contractService.createContracts(customerId, createContractsRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(contractDTOS);
	}

	@Override
	public ResponseEntity<Void> deleteContract(String contractId) {
		contractService.deleteContract(contractId);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> deleteContracts(Long customerId) {
		contractService.deleteContracts(customerId);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<ContractDTO> getContract(String contractId) {
		VersionModel<ContractDTO> contractDTOVersionModel = contractService.getContract(contractId);

		return new CustomerETagResponseEntity<>(contractDTOVersionModel).ok();
	}

	@Override
	public ResponseEntity<ListContractsResponseDTO> getContracts(Long customerId, Integer pageNumber, Integer pageSize, String sort) {
		ListContractsResponseDTO listContractsResponseDTO = contractService.getContracts(customerId,
				PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listContractsResponseDTO);
	}

	@Override
	public ResponseEntity<ContractDTO> updateContract(String contractId, String ifMatch, UpdateContractRequestDTO updateContractRequestDTO) {
		ContractDTO contractDTO = contractService.updateContract(contractId, ifMatch, updateContractRequestDTO);
		return ResponseEntity.ok(contractDTO);
	}

}