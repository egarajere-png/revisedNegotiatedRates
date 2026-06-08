package com.abcbank.negotiatedrates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Negotiated Rates Spring Boot application.
 *
 * This class boots the application context and starts the embedded server.
 */
@SpringBootApplication
public class NegotiatedratesApplication {

	/**
	 * Main method that launches the Spring Boot application.
	 *
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(NegotiatedratesApplication.class, args);
	}
}