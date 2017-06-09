package com.biddie.entity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
//import java.util.String;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;

import com.biddie.BiddieCustomException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name="Inventory")
//@Table(name="Inventory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(exclude = {"itemNo","currentBid","createdOn","updatedOn"})
public class Items {
	
	@NotNull
	@Column(name="Emailid")
	private String emailid;
	@JsonIgnore
	public String getEmailid() {
		return emailid;
	}
	@JsonProperty("emailid")
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}
	@NotNull
@Column(name="Item_Name")
private String itemName;
	@NotNull
	@Id
@Column(name="Item_No")
private String itemNo;
	@NotNull
@Column(name="Description")
private String description;
	@NotNull
	//@JsonFormat(shape=Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss")
	//@DateTimeFormat(pattern="dd-MM-yyyyTHH:mm:ss")
@Column(name="Start_Auction")
private String startAuction;
//@DateTimeFormat(pattern="dd-MM-yyyyTHH:mm:ss")
	//@JsonFormat(shape=Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss")
@NotNull
@Column(name="End_Auction")
private String endAuction;
@NotNull
@Column(name="Base_Price")
private String basePrice;
public String getBasePrice() {
	return basePrice;
}
public void setBasePrice(String basePrice) {
	
	this.basePrice = basePrice+" $";
}
@Column(insertable=false,name="Current_Bid")
private String currentBid;
@JsonFormat(shape=Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
@CreationTimestamp 
 	@Column(name = "Created_on") 
 	private Timestamp createdOn; 
@JsonFormat(shape=Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
 	@UpdateTimestamp 
 	@Column(name = "Updated_on") 
 	private Timestamp updatedOn;
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemNo() {
		return itemNo;
	}
	public void setItemNo(String itemNo) {
		
		this.itemNo = itemNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		
		this.description = description;
	}
	//@JsonFormat(shape=Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss")
	public String getStartAuction() {
		return startAuction;
	}
	public void setStartAuction(String startAuction) throws BiddieCustomException {
	if(checkDate(startAuction)){
		this.startAuction =startAuction;}
		//this.startAuction = startAuction;
	}
	public String getEndAuction() {
		return endAuction;
	}
	public void setEndAuction(String endAuction) throws BiddieCustomException {
		if(checkDate(endAuction)){
		this.endAuction = endAuction;}
	}

	public String getCurrentBid() {
		//if(BidList.)
		return currentBid;
	}
	public void setCurrentBid(String currentBid) {
		this.currentBid = currentBid;
	}
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public Timestamp getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	} 
	@Override
public boolean equals(Object i){
		if(i instanceof Items||i!=null){
			Items check=(Items) i;
			if((this.basePrice.equals(check.basePrice))&&(this.itemName.equals(check.itemName))&&(this.startAuction.compareTo(check.startAuction)==0)&&(this.endAuction.compareTo(check.endAuction)==0)){
				return true;
			}
			else 
				return false;
		}
	return false;
	
}
	public boolean checkDate(String date) throws BiddieCustomException{
	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	formatter.setLenient(false);
	boolean check=false;
	try{
		formatter.parse(date);
		check=true;
	}
	catch(Exception e){
		throw new BiddieCustomException("date should be in yyyy-MM-dd HH:mm:ss");
		
	}
	return check;
	}
}
