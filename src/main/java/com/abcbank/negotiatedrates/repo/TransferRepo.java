package com.abcbank.negotiatedrates.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.abcbank.negotiatedrates.entities.Transfer;

public interface TransferRepo extends CrudRepository<Transfer, Integer>{
	List<Transfer> findAll();
	Transfer findById(int id);
}