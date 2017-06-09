package com.biddie.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;

@Entity(name="BidList")
@EqualsAndHashCode(exclude="id")
public class BidList {
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
@Column(name="ID")
private int id;
@NotNull
@Column(name="name")
private String name;
@NotNull
@Column(name="email_id")
private String email_id;
@NotNull
@Column(name="bid_amount")
private Long bid;
@NotNull
@Column(name="item_no")
private String item_no;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail_id() {
	return email_id;
}
public void setEmail_id(String email_id) {
	this.email_id = email_id;
}
public Long getBid() {
	return bid;
}
public void setBid(Long bid) {
	this.bid = bid;
}
public String getItem_no() {
	return item_no;
}
public void setItem_no(String item_no) {
	this.item_no = item_no;
}
public String toString(){
	return String.valueOf(this.getBid());
}
}
