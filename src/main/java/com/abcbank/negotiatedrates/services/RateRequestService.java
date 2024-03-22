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

@Component
@Slf4j
public class RateRequestService {

	@Autowired
	private RateRequestRepo rateRequestRepo;

	public RateRequest saveRateRequest(DTORateRequest dtoRateRequest) {
		RateRequest rateRequest = new RateRequest();
		if(dtoRateRequest.getId() > 0) {
			rateRequest = rateRequestRepo.findById(dtoRateRequest.getId());
			//Confirm correct entity being editted by checking entities uuid
			if(rateRequest.getUuid() == null) return new RateRequest();
			if(!rateRequest.getUuid().equals(dtoRateRequest.getUuid())) return new RateRequest();
		} else {
			UUID uuid = UUID.randomUUID();
			rateRequest.setUuid(uuid.toString());
			rateRequest.setStatus((byte)0);
		}

		rateRequest = mapEntity(dtoRateRequest, rateRequest);
		
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

	public RateRequest findPendingCustomerRateRequest(String custId) {
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, (byte)0);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}
	
	public RateRequest findPendingCustomerRateAccept(String custId) {
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, (byte)1);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}

	public RateRequest findPendingCustomerRateAcceptByCustId(String custId) {
		List<RateRequest> rateRequests = rateRequestRepo.findByCustIdAndStatus(custId, (byte)1);
		if(rateRequests.size() > 0)
			return rateRequests.get(0);
		else
			return new RateRequest();
	}
	
	public RateRequestRepo getRateRequestRepo() {
		return rateRequestRepo;
	}
}
