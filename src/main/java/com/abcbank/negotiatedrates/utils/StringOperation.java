package com.abcbank.negotiatedrates.utils;

import org.springframework.stereotype.Component;

@Component
public class StringOperation {
public static void main(String[] args) {
    System.out.println(formatPhoneNumber("0720 317 929"));
}
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
