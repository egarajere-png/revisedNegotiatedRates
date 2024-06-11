package com.abcbank.negotiatedrates.services;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abcbank.negotiatedrates.dto.DTOTransfer;
import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.entities.Transfer;
import com.abcbank.negotiatedrates.repo.RateRequestRepo;
import com.abcbank.negotiatedrates.repo.TransferRepo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransferService {
	
	@Autowired
	private RateRequestRepo rateRequestRepo;
	
	@Autowired
	private TransferRepo transferRepo;
	
	public Transfer saveTransfer(DTOTransfer dtoTransfer) {
		Transfer transfer = new Transfer();
		if(dtoTransfer.getId() > 0) {
			transfer = transferRepo.findById(dtoTransfer.getRateRequestId()); 
		}
		transfer = mapEntity(dtoTransfer, transfer);
		
		List<RateRequest> rateRequestList = rateRequestRepo.findNegotiatedRate(dtoTransfer.getSourceAccount(), dtoTransfer.getSourceCurrency(), 
				dtoTransfer.getDestinationCurrency(),dtoTransfer.getAmount());
		
		log.info("\n ============ rateRequestList: {}", rateRequestList);
				
		if(rateRequestList.size() > 0) {
			RateRequest rateRequest = rateRequestList.get(rateRequestList.size() - 1);
			transfer.setRateRequest(rateRequest);
		} else {
			return new Transfer();
		}
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		transfer.setEdittedOn(ts);
		if(transfer.getCreatedOn() == null)
			transfer.setCreatedOn(ts);
		try {
			transfer = transferRepo.save(transfer);
		    return transfer;
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public Transfer mapEntity(DTOTransfer dtoTransfer, Transfer transfer) {
		log.info("\n\n ================ Tranfer: {} ===========", dtoTransfer);
		transfer.setId(dtoTransfer.getId());
		transfer.setAmount(dtoTransfer.getAmount());
		transfer.setCreatedBy(dtoTransfer.getCreatedBy());
		transfer.setRecipientName(dtoTransfer.getRecipientName());
		transfer.setRecipientAddress(dtoTransfer.getRecipientAddress());
		transfer.setRecipientBank(dtoTransfer.getRecipientBank());
		transfer.setRecipientBankAddress(dtoTransfer.getRecipientBankAddress());
		transfer.setSourceAccount(dtoTransfer.getSourceAccount());
		transfer.setSourceCurrency(dtoTransfer.getSourceCurrency());
		transfer.setDestinationCurrency(dtoTransfer.getDestinationCurrency());
		return transfer;
	}
	
	public TransferRepo getTransferRepo() {
		return transferRepo;
	}
}
