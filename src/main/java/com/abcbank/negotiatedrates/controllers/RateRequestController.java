package com.abcbank.negotiatedrates.controllers;

import java.net.URLDecoder;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.negotiatedrates.dto.DTORateApproval;
import com.abcbank.negotiatedrates.dto.DTORateRequest;
import com.abcbank.negotiatedrates.dto.DTORateResponse;
import com.abcbank.negotiatedrates.dto.DTOResponse;
import com.abcbank.negotiatedrates.dto.DTOTransfer;
import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.entities.Transfer;
import com.abcbank.negotiatedrates.services.AppNotification;
import com.abcbank.negotiatedrates.services.RateRequestService;
import com.abcbank.negotiatedrates.services.TransferService;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles creation, approval, retrieval and management of negotiated rate
 * requests between customers and treasury officers.
 *
 * This controller exposes REST endpoints used by customers to request
 * negotiated exchange rates, treasury users to grant or reject requests,
 * and customers to post transfers and query negotiated rate offers.
 *
 * Security:
 * Protected using Keycloak JWT authentication
 * Uses Spring Security role-based authorization
 * CUSTOMER users can create and view requests and transfers
 * TREASURER users can approve requests
 */
@Slf4j
@RestController
@RequestMapping("/negotiated-rates")
public class RateRequestController {

	@Value("${params.keycloak.config.token-url}")
	String KEYCLOAK_URL;

	@Value("${params.keycloak.config.clientid}")
	private String keyCloakClientId;

	@Autowired
	private RateRequestService rateRequestService;

	@Autowired
	private TransferService transferService;

	@Autowired
	private AppNotification appNotification;


	/**
	 * Creates a new negotiated rate request for a customer.
	 *
	 * Validates currency pair rules and checks for duplicate pending requests.
	 * If no duplicate pending request exists, the request is persisted and a
	 * notification is sent to treasury.
	 *
	 * @param dtoRateRequest Request payload submitted by customer
	 * @return Response indicating success or failure
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/api/rate-requesting")
	public DTOResponse postRequest(@RequestBody DTORateRequest dtoRateRequest) {
		log.info("\n==================== Posting request {} =====================\n", dtoRateRequest);
		DTOResponse response = new DTOResponse();
		String srcCurr = dtoRateRequest.getSourceCurrency();
		String dstCurr = dtoRateRequest.getDestinationCurrency();
		
		// Ensure that one side of the currency pair is KES for negotiated rate eligibility.
		if(!srcCurr.equalsIgnoreCase("404") && !srcCurr.equalsIgnoreCase("KES") 
				&& !dstCurr.equalsIgnoreCase("404") && !dstCurr.equalsIgnoreCase("KES")) {
			response.setResponseCode("004");
			response.setMessage("Either the source or destination currency must be KES");
			return response;
		}
		
		if(srcCurr.equalsIgnoreCase(dstCurr)) {
			response.setResponseCode("004");
			response.setMessage("The source currency cannot be the same as the destination currency");
			return response;
		}
		
		List<RateRequest> requests = rateRequestService.getRateRequestRepo().findExistingNegotiatedRateRequest(
				dtoRateRequest.getCustId(),
				dtoRateRequest.getSourceCurrency(), dtoRateRequest.getDestinationCurrency());

		log.info("\n ================= Requests: {}", requests);
		response.setError(true);
		if (requests.size() > 0) {
			response.setResponseCode("004");
			response.setMessage("There is a pending rate request. Contact ABC treasury.");
		} else {
			requests = rateRequestService.getRateRequestRepo().findNegotiatedRate(dtoRateRequest.getCustId(),
					dtoRateRequest.getSourceCurrency(), dtoRateRequest.getDestinationCurrency(), 0);
			if (requests.size() == 0) {
				RateRequest request = rateRequestService.saveRateRequest(dtoRateRequest);
				if (request.getId() > 0) {
					response.setError(false);
					response.setResponseCode("000");
					response.setMessage("Rate request posted");
					appNotification.sendRateRequestNotification(request, "request");
				} else {
					response.setError(true);
					response.setResponseCode("104");
					response.setMessage("Request not posted, error occured");
				}
			} else {
				response.setResponseCode("004");
				response.setMessage("You have non-utilized negotiated rate.");
			}
		}
		return response;
	}

	/**
	 * Grants a negotiated rate request from the treasury side.
	 *
	 * Validates approval input, saves granted rate details, and sends an email
	 * notification to the requester when the request is successfully granted.
	 *
	 * @param dtoRateApproval Approval payload containing granted rate and limit
	 * @return Response indicating success or failure
	 */
	@PreAuthorize("hasRole('TREASURER')")
	@PostMapping("/api/rate-granting")
	public DTOResponse approveRequest(@RequestBody DTORateApproval dtoRateApproval) {
		log.info("==================== Posting approval {} =====================\n", dtoRateApproval);
		DTOResponse response = new DTOResponse();
		if(dtoRateApproval.getGrantedAmountLimit() == null || dtoRateApproval.getGrantedRate() == null) {
			response.setResponseCode("004");
			response.setMessage("Rate request not granted, either rate or amount limit is missing");
			return response;
		}
		RateRequest request = rateRequestService.saveRateApproval(dtoRateApproval);
		
		response.setError(true);
		if (request.getId() > 0) {
			response.setError(false);
			response.setResponseCode("000");
			response.setMessage("Rate request successfully granted");
			// Sending email notification to requester
			appNotification.sendRateRequestNotification(request, "granted");
		} else {
			response.setResponseCode("004");
			response.setMessage("Rate request not granted, error occured");
		}
		return response;
	}

	/**
	 * Posts transfer details after a rate request has been approved.
	 *
	 * Persists the transfer record and returns a success or error response
	 * based on whether the transfer was saved successfully.
	 *
	 * @param dtoTransfer Transfer payload with amount and beneficiary details
	 * @return Response indicating success or failure
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/api/transfer-posting")
	public DTOResponse postTransfer(@RequestBody DTOTransfer dtoTransfer) {
		log.info("\n==================== Posting transfer {} =====================\n", dtoTransfer);
		Transfer transfer = transferService.saveTransfer(dtoTransfer);
		DTOResponse response = new DTOResponse();
		response.setError(true);
		if (transfer.getId() > 0) {
			response.setError(false);
			response.setResponseCode("000");
			response.setMessage("Transfer successfully posted");
		} else {
			response.setResponseCode("004");
			response.setMessage("Error occured");
		}
		return response;
	}

	/**
	 * Retrieves a negotiated rate offer that remains valid for the provided transfer.
	 *
	 * Uses the customer ID, currency pair, and requested amount to find a granted
	 * negotiated rate that still has available limit today.
	 *
	 * @param dtoTransfer Transfer payload used to locate the negotiated rate
	 * @return Negotiated rate details for the transfer
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/api/granted-rate")
	public DTORateResponse findNegotiatedRate(@RequestBody DTOTransfer dtoTransfer) {
		log.info("\n==================== Getting negotiated rate {} =====================\n", dtoTransfer);
		List<RateRequest> requestList = rateRequestService.getRateRequestRepo().findNegotiatedRate(
				dtoTransfer.getCustId(),
				dtoTransfer.getSourceCurrency(), dtoTransfer.getDestinationCurrency(), dtoTransfer.getAmount());
		log.info("\n==================== Calculated negotiated rate {} =====================\n", requestList);
		DTORateResponse response = new DTORateResponse();
		if (requestList.size() > 0) {
			RateRequest rateRequest = requestList.get(requestList.size() - 1);
			response.setAmountLimit(rateRequest.getGrantedAmountLimit());
			response.setGrantedRate(rateRequest.getGrantedRate());
			response.setCustId(rateRequest.getCustId());
			response.setNegotiatedRateId(rateRequest.getId());
			response.setSourceCurrency(rateRequest.getSourceCurrency());
			response.setDestinationCurrency(rateRequest.getDestinationCurrency());
		}
		return response;
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/api/pending-accept-rate/{custId}")
	public DTORateResponse findPendingAcceptRateByCustomer(@PathVariable String custId) {
		log.info("\n==================== Getting negotiated rate for custId {} =====================\n", custId);
		RateRequest rateRequest = rateRequestService.findPendingCustomerRateAccept(custId);
		DTORateResponse response = new DTORateResponse();
		if (rateRequest.getId() > 0) {
			response.setAmountLimit(rateRequest.getGrantedAmountLimit());
			response.setGrantedRate(rateRequest.getGrantedRate());
			response.setCustId(rateRequest.getCustId());
			response.setNegotiatedRateId(rateRequest.getId());
			response.setSourceCurrency(rateRequest.getSourceCurrency());
			response.setDestinationCurrency(rateRequest.getDestinationCurrency());
			response.setAppeal(rateRequest.isAppeal());
			// appNotification.sendRateRequestNotification(rateRequest, "accept");
		}
		return response;
	}

	/**
	 * Finds a pending accepted rate for a customer ID that may contain encoded or
	 * comma-separated values.
	 *
	 * Decodes the path variable, sanitizes values, and searches for the first
	 * pending customer acceptance rate across the supplied identifiers.
	 *
	 * @param custId Encoded customer identifier(s) from the path
	 * @return Negotiated rate response for the first matching pending accept request
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/api/customer-pending-accept-rate/{custId}")
	public DTORateResponse findPendingAcceptRateByCustId(@PathVariable String custId) {
		DTORateResponse response = new DTORateResponse();
		try {
			custId = URLDecoder.decode(custId, "utf-8");
			custId = custId.replaceAll("'", "");
			custId = custId.split(",")[0];
			log.info("\n==================== Getting negotiated rate for custId {} =====================\n", custId);
			RateRequest rateRequest = null;
			for (String custId2 : custId.split(",")) {
				// Search each provided customer identifier until a pending accept request is found.
				rateRequest = rateRequestService.findPendingCustomerRateAcceptByCustId(custId2);
				if (rateRequest != null)
					break;
			}
			if (rateRequest != null) {
				response.setAmountLimit(rateRequest.getGrantedAmountLimit());
				response.setGrantedRate(rateRequest.getGrantedRate());
				response.setCustId(rateRequest.getCustId());
				response.setNegotiatedRateId(rateRequest.getId());
				response.setSourceCurrency(rateRequest.getSourceCurrency());
				response.setDestinationCurrency(rateRequest.getDestinationCurrency());
				response.setAppeal(rateRequest.isAppeal());
				// appNotification.sendRateRequestNotification(rateRequest, "accept");
			}
			log.info("\n==================== Response for custId {}: {}=====================\n", custId, response);
		} catch (Exception e) {
		}

		return response;
	}

	/**
	 * Accepts or rejects a negotiated rate offer on behalf of a customer.
	 *
	 * Updates the pending request status based on the provided action and sends
	 * a corresponding notification for accepted or rejected outcomes.
	 *
	 * @param custId Customer identifier associated with the pending rate request
	 * @param action Action to perform, typically "Accept" or another string for rejection
	 * @return Updated RateRequest entity after status change
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/api/rate-accepting/{custId}/{action}")
	public RateRequest acceptRate(@PathVariable String custId, @PathVariable String action) {
		log.info("\n==================== Accepting/appealing rate - custId: {}, action: {} =====================\n",
				custId, action);
		RateRequest rateRequest = rateRequestService.findPendingCustomerRateAccept(custId);

		log.info("\n ========================= This is RateRequest: {} =============== \n", rateRequest);
		log.info("=============== Checking if rate request exists");
		if (rateRequest.getId() == 0) {
			return new RateRequest();
		}
		log.info("=============== Rate request exists");
		log.info("=============== About to pick action: {}", action);
		byte status = 3;
		// Map the action to the request acceptance status.
		status = action.equalsIgnoreCase("Accept") ? (byte) 2 : status;
		log.info("=============== About to pick action, status: {}", status);
		log.info("============= Status: {}::::::\n\n", status);
		rateRequest.setStatus(status);
		rateRequestService.getRateRequestRepo().save(rateRequest);
		if (status == (byte) 2) {
			appNotification.sendRateRequestNotification(rateRequest, "accepted");
		} else {
			appNotification.sendRateRequestNotification(rateRequest, "rejected");
		}
		return rateRequest;
	}

	/**
	 * Retrieves a pending negotiated rate request for a specific customer.
	 *
	 * This endpoint is intended for customers to see their current pending rate
	 * request that is still awaiting treasury action.
	 *
	 * @param custId Customer identifier to lookup pending requests
	 * @return Negotiated rate response for the pending request
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/api/pending-rate-request/{custId}")
	public DTORateResponse findPendingRateByCustomer(@PathVariable String custId) {
		log.info("\n==================== Getting negotiated rate for custId {} =====================\n", custId);
		RateRequest rateRequest = rateRequestService.findPendingCustomerRateRequest(custId);
		DTORateResponse response = new DTORateResponse();
		if (rateRequest.getId() > 0) {
			response.setAmountLimit(rateRequest.getGrantedAmountLimit());
			response.setGrantedRate(rateRequest.getGrantedRate());
			response.setCustId(rateRequest.getCustId());
			response.setNegotiatedRateId(rateRequest.getId());
			response.setSourceCurrency(rateRequest.getSourceCurrency());
			response.setDestinationCurrency(rateRequest.getDestinationCurrency());
			response.setAppeal(rateRequest.isAppeal());
		}
		return response;
	}

	/**
	 * Returns a single rate request by its identifier.
	 *
	 * Accessible to treasury officers and admin users for review or action.
	 *
	 * @param id Negotiated rate request identifier
	 * @return RateRequest entity if found
	 */
	@PreAuthorize("hasAnyRole('TREASURER','ADMIN')")
	@GetMapping("/api/rate-request/{id}")
	public RateRequest getRateRequest(@PathVariable int id) {
		return rateRequestService.getRateRequestRepo().findById(id);
	}

	/**
	 * Retrieves all pending negotiated rate requests that are waiting for treasury approval.
	 *
	 * @return List of pending RateRequest entities with status 0
	 */
	@PreAuthorize("hasRole('TREASURER')")
	@GetMapping("/api/pending-rate-request")
	public List<RateRequest> getPendingRequests() {
		return rateRequestService.getRateRequestRepo().findByStatus((byte) 0);
	}

	/**
	 * A simple admin, treasurer and Customer endpoint used to verify their access.
	 *
	 * @return Confirmation string when the request is authorized
	 */

	// WAS USED FOR TESTING PURPOSES
// 	@GetMapping("/test-admin")
// 	@PreAuthorize("hasRole('ADMIN')")
// 	public String testAdmin() {
// 		return "Admin access granted";
// 	}

// 	@GetMapping("/test-treasurer")
// 	@PreAuthorize("hasRole('TREASURER')")
// 	public String testTreasurer() {
//   	  return "Treasurer access granted";
// }

// 	@GetMapping("/test-customer")
// 	@PreAuthorize("hasRole('CUSTOMER')")
// 	public String testCustomer() {
//  	   return "Customer access granted";
// }
}
