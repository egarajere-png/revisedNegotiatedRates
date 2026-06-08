package com.abcbank.negotiatedrates.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abcbank.negotiatedrates.dto.DTORateApproval;
import com.abcbank.negotiatedrates.dto.DTORateRequest;
import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.repo.RateRequestRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * Encapsulates negotiated rate request business logic and repository interaction.
 *
 * This component validates new requests, saves approval decisions, and provides
 * helper methods to retrieve pending customer and treasury requests.
 */
@Component
@Slf4j
public class RateRequestService {

	@Autowired
	private RateRequestRepo rateRequestRepo;

	/**
	 * Saves a negotiated rate request to the repository.
	 *
	 * If the payload contains an existing ID, the corresponding record is loaded
	 * and validated against its UUID to prevent unauthorized edits.
	 * Otherwise, a new request is created with initial status and UUID.
	 *
	 * @param dtoRateRequest Customer request payload
	 * @return Persisted RateRequest entity or an empty RateRequest on failure
	 */
	public RateRequest saveRateRequest(DTORateRequest dtoRateRequest) {
		RateRequest rateRequest = new RateRequest();
		if(dtoRateRequest.getId() > 0) {
			rateRequest = rateRequestRepo.findById(dtoRateRequest.getId());
			// Confirm correct entity being edited by checking entity UUID.
			if(rateRequest.getUuid() == null) return new RateRequest();
			if(!rateRequest.getUuid().equals(dtoRateRequest.getUuid())) return new RateRequest();
		} else {
			UUID uuid = UUID.randomUUID();
			rateRequest.setUuid(uuid.toString());
			rateRequest.setStatus((byte)0);
		}

		rateRequest = mapEntity(dtoRateRequest, rateRequest);
		
		// Ensure required request fields are present and amount limit is valid.
		if(rateRequest.getCustId() == null || rateRequest.getSourceCurrency() == null || rateRequest.getDestinationCurrency() == null
				 || rateRequest.getRequestedAmountLimit() < 0.1) {
			return new RateRequest();
		}
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		rateRequest.setEdittedOn(ts);
		if(rateRequest.getCreatedOn() == null)
			rateRequest.setCreatedOn(ts);
		try {
			log.info("\n\nJust about to persist: {}", rateRequest);
			rateRequest = rateRequestRepo.save(rateRequest);
			return rateRequest;
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		return new RateRequest();
	}

	/**
	 * Persists approval data for an existing negotiated rate request.
	 *
	 * Validates the UUID to ensure the approval applies to the correct record,
	 * then updates granted amount, granted rate, granted by, and status.
	 *
	 * @param dtoRateApproval Approval payload from the treasury user
	 * @return Updated RateRequest entity or an empty RateRequest on failure
	 */
	public RateRequest saveRateApproval(DTORateApproval dtoRateApproval) {
		RateRequest rateRequest = new RateRequest();
		if(dtoRateApproval.getId() > 0) {
			rateRequest = rateRequestRepo.findById(dtoRateApproval.getId()); 
			if(rateRequest != null) {
				if(rateRequest.getUuid() == null) return new RateRequest();
				if(!rateRequest.getUuid().equals(dtoRateApproval.getUuid())) return new RateRequest();
				rateRequest.setGrantedAmountLimit(dtoRateApproval.getGrantedAmountLimit());
				rateRequest.setGrantedRate(dtoRateApproval.getGrantedRate());
				rateRequest.setGrantedBy(dtoRateApproval.getGrantedBy());
				rateRequest.setStatus((byte)dtoRateApproval.getStatus());
			}
			try {
				rateRequest = rateRequestRepo.save(rateRequest);
			} catch(Exception e) {
				log.error(e.getMessage());
				rateRequest = new RateRequest();
			}
		}
		return rateRequest;
	}

	/**
	 * Copies fields from the DTO request into the entity.
	 *
	 * This mapping isolates API payload structure from persistence structure.
	 *
	 * @param dtoRateRequest Incoming request DTO
	 * @param request Domain entity to populate
	 * @return Populated RateRequest entity
	 */
	public RateRequest mapEntity(DTORateRequest dtoRateRequest, RateRequest request) {
		request.setId(dtoRateRequest.getId());
		request.setRequestedAmountLimit(dtoRateRequest.getAmountLimit());
		request.setCustId(dtoRateRequest.getCustId());
		request.setRequestedBy(dtoRateRequest.getRequestedBy());
		request.setRequestedRate(dtoRateRequest.getRequestedRate());
		request.setCustomerName(dtoRateRequest.getRequestedBy());
		request.setSourceCurrency(dtoRateRequest.getSourceCurrency());
		request.setDestinationCurrency(dtoRateRequest.getDestinationCurrency());
		request.setTransferType(dtoRateRequest.getTransferType());
		request.setNotificationEmail(dtoRateRequest.getNotificationEmail());
		return request;
	}

	/**
	 * Finds a rate request that is pending treasury review for a customer.
	 *
	 * Uses status 0 to represent requests awaiting treasury approval.
	 *
	 * @param custId Customer identifier
	 * @return First matching pending RateRequest or an empty RateRequest
	 */
	public RateRequest findPendingCustomerRateRequest(String custId) {
		byte pendingTreasuryStatus = 0;
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, pendingTreasuryStatus);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}
	
	/**
	 * Finds a rate request that is pending customer acceptance.
	 *
	 * Uses status 1 to represent requests that have been granted and await
	 * customer acceptance or rejection.
	 *
	 * @param custId Customer identifier
	 * @return First matching pending acceptance RateRequest or an empty RateRequest
	 */
	public RateRequest findPendingCustomerRateAccept(String custId) {
		byte pendingCustomerStatus = 1;
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, pendingCustomerStatus);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}

	/**
	 * Finds a pending accepted rate request by customer ID.
	 *
	 * This method is used when multiple customer identifiers are provided and
	 * the first match should be returned.
	 *
	 * @param custId Customer identifier
	 * @return Matching RateRequest or an empty RateRequest
	 */
	public RateRequest findPendingCustomerRateAcceptByCustId(String custId) {
		byte pendingCustomerStatus = 1;
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, pendingCustomerStatus);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}
	
	/**
	 * Exposes the rate request repository for controller and service use.
	 *
	 * @return RateRequestRepo instance for querying persisted rate requests
	 */
	public RateRequestRepo getRateRequestRepo() {
		return rateRequestRepo;
	}
}
