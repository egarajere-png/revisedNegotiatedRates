package com.abcbank.negotiatedrates.utils;

import org.springframework.stereotype.Component;

/**
 * Utility class for common string operations used across the application.
 */
@Component
public class StringOperation {
public static void main(String[] args) {
    System.out.println(formatPhoneNumber("0720 317 929"));
}
	/**
	 * Converts text to sentence case, capitalizing the first letter of each word.
	 *
	 * @param text Input string to format
	 * @return String with each word capitalized, or the original text on error
	 */
	public static String changeToSentenceCase(String text) {
		try {
			text = text.toLowerCase().trim();
			String[] words = text.split(" ");
			text = "";
			for(String word : words) {
				text += text.length() == 0 ? "" : " ";
				text += word.substring(0,1).toUpperCase() + word.substring(1,word.length()).trim();
			}
		} catch(Exception e) {
		}
		return text;
	}

	/**
	 * Formats a phone number to the 254 country code format.
	 *
	 * @param phoneNumber Input phone number string
	 * @return Phone number in 254XXXXXXXXX format or null if invalid
	 */
	public static String formatPhoneNumber(String phoneNumber) {
		try {
			phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
			if(phoneNumber.length() >= 9) {
				phoneNumber	= "254" + phoneNumber.substring(phoneNumber.length() - 9, phoneNumber.length());
				return phoneNumber.trim();
			} else {
				return null;
			}
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Extracts the first word from a string and formats it in sentence case.
	 *
	 * @param input Input string from which to extract the first word
	 * @return First word formatted in sentence case
	 */
	public static String getFirstWord(String input) {
		try {
		    String firstWord = input.substring(0,input.indexOf(" "));
		    return changeToSentenceCase(firstWord);
		} catch(StringIndexOutOfBoundsException e) {
			return changeToSentenceCase(input);
		} catch(Exception e) {
			return input;
		}
	}

	/**
	 * Determines whether a string represents a valid numeric value.
	 *
	 * @param string Input string to validate
	 * @return true if the string can be parsed as a double
	 */
	public static boolean isNumeric(String string) {
		if (string == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(string);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
