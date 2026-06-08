package com.abcbank.negotiatedrates.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.abcbank.negotiatedrates.entities.Transfer;

/**
 * Repository for transfer records associated with negotiated rate orders.
 *
 * Provides lookup operations over persisted transfer entities.
 */
public interface TransferRepo extends CrudRepository<Transfer, Integer>{
	List<Transfer> findAll();

	/**
	 * Retrieves a transfer entity by its identifier.
	 *
	 * @param id Transfer record identifier
	 * @return Transfer entity if present
	 */
	Transfer findById(int id);
}