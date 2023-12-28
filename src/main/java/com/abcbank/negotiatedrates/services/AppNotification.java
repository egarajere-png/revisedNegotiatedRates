package com.abcbank.negotiatedrates.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.utils.Emailer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppNotification {

    @Autowired
    private Emailer emailer;

    public void sendRateRequestNotification(RateRequest request, String type) {
        String subject = "Negotiated Rate Request Initiated";
        String body = "Hello, your negotiated rate request has been successfully initiated. You will be receiving a response soon.";
		double rate = request.getGrantedRate();
        if(type.equalsIgnoreCase("granted")) {
            subject = "Negotiated Rate Request Granted";
            body = String.format("Hello, you have been granted a negotiated rate of %d. To accept the offer, go to ABConnect, "
            + "on transfers select 'Accept Rate', the account and submit", rate);
        } else if(type.equalsIgnoreCase("accepted")) {
            subject = "Negotiated Rate Request Accepted";
            body = String.format("Hello, you have accepted the granted negotiated rate of %d. You can now go ahead and transact, the rate "
            + "will be applied on your transaction automatically", rate);
        }
		String from = "internalsupport@abcthebank.com";
		String to = request.getNotificationEmail();
		emailer.send(from, to, subject, body);
	}
}
