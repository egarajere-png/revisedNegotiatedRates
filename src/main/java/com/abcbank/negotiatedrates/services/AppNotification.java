package com.abcbank.negotiatedrates.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abcbank.negotiatedrates.entities.RateRequest;
import com.abcbank.negotiatedrates.utils.Emailer;
import com.abcbank.negotiatedrates.utils.StringOperation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppNotification {

    
    @Autowired
    private Emailer emailer;
    
    @Value("${app.config.param.email-from}")
    private String from;

    public static void main(String[] args) {
    	String st2 = "This is numbers %1$,.0f and %1$,.0f";
		System.out.println(String.format(st2, 10*1.0, 10000.1));
		
		String st = "This is string %1$s, %1$s and %2$s. Also numbers %f";
		//System.out.println(String.format(st, "Samuel", "Waithaka", 100.1));
	}
    
    public void sendRateRequestNotification(RateRequest request, String type) {
        String name = StringOperation.getFirstWord(request.getRequestedBy());
        String subject = "Negotiated Rate Request Initiated";
        String body = String.format("Hello %s, your negotiated rate request has been successfully initiated for customer name %s, "
        + "currency %s to %s. You will be receiving a response soon.", name, request.getCustomerName(), request.getSourceCurrency(), request.getDestinationCurrency());

        String bodyTreasury = String.format("Hello, a negotiated rate request has been initiated by %s, customer name %s, "
        + "currency %s to %s. Kindly action on Negotiated Rates Portal.", request.getRequestedBy(), request.getCustomerName(), request.getSourceCurrency(), request.getDestinationCurrency());
        if(type.equalsIgnoreCase("granted")) {
            double rate = request.getGrantedRate();
            subject = "Negotiated Rate Request Granted";
            body = String.format("Hello %s, \n\nYou have been granted a negotiated rate of %,.2f for the customer name %s, currency %s to %s. To accept the rate offered, go to ABConnect, "
            + "on transfers select 'Accept Grated Rate', the account and submit", name, rate, request.getCustomerName(), request.getSourceCurrency(), request.getDestinationCurrency());
            bodyTreasury = "";
        } else if(type.equalsIgnoreCase("accepted")) {
            double rate = request.getGrantedRate();
            subject = "Negotiated Rate Offer Accepted";
            body = String.format("Hello %s, \n\nYou have accepted the granted negotiated rate of %,.2f, for customer name %s, currency %s to %s. You can now go ahead and transact, the rate "
            + "will be applied on your transaction automatically. You can transact upto %s%,.2f with this rate", name, rate, request.getCustomerName(), request.getSourceCurrency(), request.getDestinationCurrency(), request.getDestinationCurrency(), request.getGrantedAmountLimit());
        } else if(type.equalsIgnoreCase("rejected")) {
            double rate = request.getGrantedRate();
            subject = "Negotiated Rate Offer Rejected";
            body = String.format("Hello %s, \n\nYou have rejected the granted negotiated rate of %,.2f, for customer name %s, currency %s to %s.", 
            name, rate, request.getCustomerName(), request.getSourceCurrency(), request.getDestinationCurrency());
        } else {
            String to = "treasury@abcthebank.com";
            emailer.send(from, to, subject, bodyTreasury); 
        }
        log.info(" ==================== Type: {}, Body: {}", type, body);
		String to = request.getNotificationEmail();
		emailer.send(from, to, subject, body);
	}
}
