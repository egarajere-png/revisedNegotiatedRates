package com.abcbank.negotiatedrates.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.negotiatedrates.dto.DTORateApproval;
import com.abcbank.negotiatedrates.dto.DTORateRequest;
import com.abcbank.negotiatedrates.dto.DTORateResponse;
import com.abcbank.negotiatedrates.dto.DTOResponse;
import com.abcbank.negotiatedrates.dto.DTOTransfer;
import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.entities.Transfer;
import com.abcbank.negotiatedrates.services.RateRequestService;
import com.abcbank.negotiatedrates.services.TransferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RateRequestController {

	@Value("${params.keycloak.config.token-url}")
	String KEYCLOAK_URL;

	@Value("${params.keycloak.config.clientid}")
	private String keyCloakClientId;

	@Autowired
	private RateRequestService rateRequestService;

	@Autowired
	private TransferService transferService;

	@PostMapping("/negotiated-rates/api/post-request")
	public DTOResponse postRequest(@RequestBody DTORateRequest dtoRateRequest) {
		log.info("\n==================== Posting request {} =====================\n", dtoRateRequest);

		List<RateRequest> requests = rateRequestService.getRateRequestRepo().findPendingNegotiatedRate(dtoRateRequest.getSourceAccount(), 
				dtoRateRequest.getSourceCurrency(), dtoRateRequest.getDestinationCurrency());

		DTOResponse response = new DTOResponse();
		response.setError(true);
		if(requests.size() > 0) {
			response.setResponseCode("004");
			response.setMessage("There is a pending rate request. Contact ABC treasury.");
		} else {
			RateRequest request = rateRequestService.saveRateRequest(dtoRateRequest);
			if(request.getId() > 0) {
				response.setError(false);
				response.setResponseCode("000");
				response.setMessage("Rate request posted");
			}
		}
		return response;
	}

	@PostMapping("/negotiated-rates/api/approve-request")
	public DTOResponse approveRequest(@RequestBody DTORateApproval dtoRateApproval) {
		log.info("\n==================== Posting approval {} =====================\n", dtoRateApproval);
		RateRequest request = rateRequestService.saveRateApproval(dtoRateApproval);
		DTOResponse response = new DTOResponse();
		response.setError(true);
		if(request.getId() > 0) {
			response.setError(false);
			response.setResponseCode("000");
			response.setMessage("Rate request posted");
		}
		return response;
	}

	@PostMapping("/negotiated-rates/api/post-transfer")
	public DTOResponse postTransfer(@RequestBody DTOTransfer dtoTransfer) {
		log.info("\n==================== Posting transfer {} =====================\n", dtoTransfer);
		Transfer transfer = transferService.saveTransfer(dtoTransfer);
		DTOResponse response = new DTOResponse();
		response.setError(true);
		if(transfer.getId() > 0) {
			response.setError(false);
			response.setResponseCode("000");
			response.setMessage("Transfer successfully posted");
		}
		return response;
	}

	@PostMapping("/negotiated-rates/api/get-negotiated-rate")
	public DTORateResponse getNegotiatedRate(@RequestBody DTOTransfer dtoTransfer) {
		log.info("\n==================== Getting negotiated rate {} =====================\n", dtoTransfer);
		List<RateRequest> requestList = rateRequestService.getRateRequestRepo().findNegotiatedRate(dtoTransfer.getSourceAccount(), 
				dtoTransfer.getSourceCurrency(),dtoTransfer.getDestinationCurrency(), dtoTransfer.getAmount());
		log.info("\n==================== Calculated negotiated rate {} =====================\n", requestList);
		DTORateResponse response = new DTORateResponse();
		if(requestList.size() > 0) {
			RateRequest rateRequest = requestList.get(requestList.size() - 1);
			response.setAmountLimit(rateRequest.getGrantedAmountLimit());
			response.setSourceAccount(rateRequest.getSourceAccount());
			response.setSourceCurrency(rateRequest.getSourceCurrency());
			response.setDestinationCurrency(rateRequest.getDestinationCurrency());
		}
		return response;
	}
}
