package com.abcbank.negotiatedrates.services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abcbank.negotiatedrates.dto.DTOTransfer;
import com.abcbank.negotiatedrates.entities.Transfer;
import com.abcbank.negotiatedrates.repo.RateRequestRepo;
import com.abcbank.negotiatedrates.repo.TransferRepo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransferService {
	
	@Autowired
	private TransferRepo transferRepo;
	
	public Transfer saveTransfer(DTOTransfer dtoTransfer) {
		Transfer transfer = new Transfer();
		if(dtoTransfer.getId() > 0) {
			transfer = transferRepo.findById(dtoTransfer.getId()); 
		}
		transfer = mapEntity(dtoTransfer, transfer);
		
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
		return transfer;
	}
	
	public TransferRepo getTransferRepo() {
		return transferRepo;
	}
}
