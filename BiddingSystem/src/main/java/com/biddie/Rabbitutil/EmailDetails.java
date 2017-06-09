package com.biddie.Rabbitutil;

import org.springframework.stereotype.Component;

@Component
public class EmailDetails {
private String emailID;
public String getEmailID() {
	return emailID;
}
public void setEmailID(String emailID) {
	this.emailID = emailID;
}
public String getMsg() {
	return msg;
}
public void setMsg(String msg) {
	this.msg = msg;
}
private String msg;

}
