package com.abcbank.negotiatedrates.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.abcbank.negotiatedrates.entities.RateRequest;

/**
 * Repository for negotiated rate requests.
 *
 * Provides CRUD operations and custom queries used to find pending requests,
 * existing negotiated rate offers, and granted rates available for transfers.
 */
public interface RateRequestRepo extends CrudRepository<RateRequest, Integer>{
	List<RateRequest> findAll();
	
	/*@Query(value = "select r.* from rate_request r where date(r.editted_on) = date(now()) and r.cust_id = ?1 "
			+ "and status = ?2 and date(r.created_on)=date(now()) order by r.id asc", nativeQuery = true)
	List<RateRequest> findByCustomerAndStatus(String custId, byte status);*/

	/**
	 * Finds rate requests for a customer with the given status that were edited today.
	 *
	 * Used to retrieve pending requests or requests awaiting customer acceptance.
	 */
	@Query(value = "select r.* from rate_request r where date(r.editted_on) = date(now()) and r.cust_id = ?1 "
			+ "and status = ?2 and date(r.created_on)=date(now()) order by r.id asc", nativeQuery = true)
	List<RateRequest> findByCustIdAndStatus(String custId, byte status);

	/**
	 * Finds existing negotiated rate requests for the same customer and currency pair.
	 *
	 * This query detects duplicate pending requests for a given customer today.
	 */
	@Query(value = "select r.* from rate_request r where status < 2 and date(r.editted_on) = date(now()) and r.cust_id = ?1 "
			+ "and r.source_currency = ?2 and r.destination_currency = ?3 and date(r.created_on)=date(now()) order by r.id asc", nativeQuery = true)
	List<RateRequest> findExistingNegotiatedRateRequest(String custId, String sourceCurrency, String destinationCurrency);
		
	RateRequest findById(int id);
	
	List<RateRequest> findByStatus(byte status);
	
	/**
	 * Finds negotiated rate offers that have been granted and still have available limit.
	 *
	 * The query subtracts today's transferred amount for the same customer and
	 * currency pair from the granted limit to determine availability.
	 */
	@Query(value = "select r.* from rate_request r \n"
			+ "where status = 2 and date(r.editted_on) = date(now()) and r.cust_id = ?1 and r.source_currency = ?2 and r.destination_currency = ?3 "
			+ "and r.granted_amount_limit - ?4 >= case when (select sum(amount) total from transfer t left join rate_request rr on t.rate_request_id = rr.id where rr.cust_id = ?1 "
			+ "and t.source_currency = ?2 and t.destination_currency = ?3 and date(t.created_on)=date(now())) is null then 0 "
			+ "else (select sum(amount) total from transfer t left join rate_request rr on t.rate_request_id = rr.id where rr.cust_id = ?1 and t.source_currency = ?2 and t.destination_currency = ?3 "
			+ "and date(t.created_on)=date(now())) end", nativeQuery = true)
	List<RateRequest> findNegotiatedRate(String custId, String sourceCurrency, String destinationCurrency, double amount);
}