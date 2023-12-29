package com.abcbank.negotiatedrates.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.abcbank.negotiatedrates.entities.RateRequest;

public interface RateRequestRepo extends CrudRepository<RateRequest, Integer>{
	List<RateRequest> findAll();
	//List<RateRequest> findBySourceAccountAndStatus(String sourceAccount, byte status);
	
	@Query(value = "select r.* from rate_request r where date(r.editted_on) = date(now()) and r.source_account = ?1 "
			+ "and status = ?2 and date(r.created_on)=date(now())", nativeQuery = true)
	List<RateRequest> findBySourceAccountAndStatus(String sourceAccount, byte status);

	@Query(value = "select r.* from rate_request r where status < 2 and date(r.editted_on) = date(now()) and r.source_account = ?1 "
			+ "and r.source_currency = ?2 and r.destination_currency = ?3 and date(r.created_on)=date(now())", nativeQuery = true)
	List<RateRequest> findExistingNegotiatedRateRequest(String sourceAccount, String sourceCurrency, String destinationCurrency);
		
	RateRequest findById(int id);
	
	List<RateRequest> findByStatus(byte status);
	
	@Query(value = "select r.* from rate_request r \n"
			+ "where status =2 and date(r.editted_on) = date(now()) and r.source_account = ?1 and r.source_currency = ?2 and r.destination_currency = ?3\n"
			+ "and r.granted_amount_limit - ?4 >= case when (select sum(amount) total from transfer t where t.source_account = ?1 "
			+ "and t.source_currency ="
			+ "?2 and t.destination_currency = ?3\n"
			+ "and date(t.created_on)=date(now())) is null then 0 else ((select sum(amount) total from transfer t where t.source_account = ?1  and t.source_currency = ?2 and t.destination_currency = ?3 \n"
			+ "and date(t.created_on)=date(now()))) end", nativeQuery = true)
	List<RateRequest> findNegotiatedRate(String sourceAccount, String sourceCurrency, String destinationCurrency, double amount);
}