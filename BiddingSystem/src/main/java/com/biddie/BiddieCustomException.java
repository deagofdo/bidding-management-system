package com.biddie;

public class BiddieCustomException extends Exception {
String message;
public String getMessage() {
	return message;
}
public BiddieCustomException(String errorMessage) {
super(errorMessage);
	this.message=errorMessage;
}
}
