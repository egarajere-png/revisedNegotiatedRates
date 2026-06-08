package com.abcbank.negotiatedrates.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Utility for sending email notifications using SMTP.
 *
 * This component constructs MimeMessage payloads and sends them using the
 * configured SMTP host value.
 */
@Component
public class Emailer {
	
	@Value("${app.config.param.smtphost}")
	private String smtpHost;
	
	/**
	 * Sends a plain HTML email to a single recipient.
	 *
	 * @param from Sender address
	 * @param to Recipient address
	 * @param subject Email subject line
	 * @param body Email body content in HTML format
	 * @return true if the message was sent successfully
	 */
	public boolean send(String from, String to, String subject, String body) {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", smtpHost);
		Session session = Session.getDefaultInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(body, "utf-8", "html");
			Transport.send(message);
			System.out.println("message sent successfully...");
			return true;
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sends an email with optional CC recipients and attachments.
	 *
	 * @param from Sender address
	 * @param to Array of recipient addresses
	 * @param cc Array of CC recipient addresses
	 * @param subject Email subject line
	 * @param body Email body content
	 * @param files Attachment file paths
	 * @return true if the message was sent successfully
	 */
	public boolean send(String from, String[] to, String[] cc, String subject, String body, String[] files) {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", smtpHost);
		Session session = Session.getDefaultInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			for(String rcpt : to) {
				System.out.println("To: " + rcpt);
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(rcpt));
			}
			
			for(String rcpt : cc) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(rcpt));
			}
			message.setSubject(subject);
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        
	        for(String file : files) {
		        messageBodyPart = new MimeBodyPart();
		        DataSource source = new FileDataSource(file);
		        messageBodyPart.setDataHandler(new DataHandler(source));
		        messageBodyPart.setFileName(file.substring(file.lastIndexOf("/") + 1,file.length()));
		        multipart.addBodyPart(messageBodyPart);
	        }
            message.setContent(multipart);
            
			Transport.send(message);
			
			System.out.println("message sent successfully...");
			return true;
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
	}
}