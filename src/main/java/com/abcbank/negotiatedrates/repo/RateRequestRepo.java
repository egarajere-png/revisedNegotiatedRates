package com.abcbank.negotiatedrates.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.abcbank.negotiatedrates.entities.RateRequest;

public interface RateRequestRepo extends CrudRepository<RateRequest, Integer>{
	List<RateRequest> findAll();
	
	@Query(value = "select r.* from rate_request r \n"
			+ "where r.editted_on::date = now()::date and r.source_account = '001190001000062' and r.source_currency = 'KES'\n"
			+ "and r.destination_currency = 'EUR'\n"
			+ "and r.created_on::date=now()::date", nativeQuery = true)
	List<RateRequest> findPendingNegotiatedRate(String sourceAccount, String sourceCurrency, String destinationCurrency);
	
	RateRequest findById(int id);
	
	@Query(value = "select r.* from rate_request r \n"
			+ "where r.editted_on::date = now()::date and r.source_account = ?1 and r.source_currency = ?2\n"
			+ "and r.destination_currency = ?3\n"
			+ "and r.granted_amount_limit - ?4 >= (select sum(amount) total from transfer t where t.source_account = ?1 and t.source_currency =?2 and t.destination_currency = ?3\n"
			+ "and t.created_on::date=now()::date)", nativeQuery = true)
	List<RateRequest> findNegotiatedRate(String sourceAccount, String sourceCurrency, String destinationCurrency, double amount);
}