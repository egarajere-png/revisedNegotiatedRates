package com.abcbank.negotiatedrates.services;

import java.sql.Timestamp;
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
			rateRequest.setStatus((byte)0);
			//Confirm correct entity being editted by checking entities uuid
			if(rateRequest.getUuid() == null) return new RateRequest();
			if(!rateRequest.getUuid().equals(dtoRateRequest.getUuid())) return new RateRequest();
		} else {
			UUID uuid = UUID.randomUUID();
			rateRequest.setUuid(uuid.toString());
		}
		rateRequest = mapEntity(dtoRateRequest, rateRequest);
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		rateRequest.setEdittedOn(ts);
		if(rateRequest.getCreatedOn() == null)
			rateRequest.setCreatedOn(ts);
		try {
			rateRequest = rateRequestRepo.save(rateRequest);
		    return rateRequest;
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public RateRequest saveRateApproval(DTORateApproval dtoRateApproval) {
		RateRequest rateRequest = new RateRequest();
		if(dtoRateApproval.getId() > 0) {
			rateRequest = rateRequestRepo.findById(dtoRateApproval.getId()); 
			//Confirm correct entity being editted by checking entities uuid
			if(rateRequest.getUuid() == null) return new RateRequest();
			if(!rateRequest.getUuid().equals(dtoRateApproval.getUuid())) return new RateRequest();
		}
		rateRequest.setGrantedAmountLimit(dtoRateApproval.getGrantedAmountLimit());
		rateRequest.setGrantedRate(dtoRateApproval.getGrantedRate());
		rateRequest.setGrantedBy(dtoRateApproval.getGrantedBy());
		rateRequest.setStatus(dtoRateApproval.getStatus());
		try {
			rateRequest = rateRequestRepo.save(rateRequest);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		return rateRequest;
	}
	
	public RateRequest mapEntity(DTORateRequest dtoRateRequest, RateRequest request) {
		request.setId(dtoRateRequest.getId());
		request.setRequestedAmountLimit(dtoRateRequest.getAmountLimit());
		request.setRequestedBy(dtoRateRequest.getRequestedBy());
		request.setRequestedRate(dtoRateRequest.getRequestedRate());
		request.setSourceAccount(dtoRateRequest.getSourceAccount());
		request.setSourceCurrency(dtoRateRequest.getSourceCurrency());
		request.setDestinationCurrency(dtoRateRequest.getDestinationCurrency());
		request.setTransferType(dtoRateRequest.getTransferType());
		return request;
	}
	
	public RateRequestRepo getRateRequestRepo() {
		return rateRequestRepo;
	}
}
