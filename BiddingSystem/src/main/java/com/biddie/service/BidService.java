package com.biddie.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.transaction.Transactional;

import org.json.simple.parser.JSONParser;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.biddie.BiddieCustomException;
import com.biddie.Rabbitutil.EmailDetails;
import com.biddie.entity.BidList;
import com.biddie.entity.Items;
import com.biddie.repo.BidRepo;
import com.biddie.repo.Inventory;
import com.google.gson.Gson;

@Service
@Transactional
public class BidService {
	@Autowired
	Inventory inventory;
	@Autowired
	BidRepo bidrep;
	@Autowired
	RabbitTemplate bugs;
	
private	EmailDetails emaildetails;
public String addBid(Items newBid) throws Exception{
	String result=null;
	System.out.println(new JSONParser().parse(new Gson().toJson(newBid)));
	if(checkdates(newBid)){
	try {List<Items>list=(List<Items>) inventory.findAll();
	if(!list.isEmpty()){
	for(Items item:list){
		System.out.println("inside loop check");
		if(item.equals(newBid)){
			//System.out.println("checkkkk"+item.equals(newBid));
			throw new BiddieCustomException("same product in the same auction date,choose different one");
		}
	}}
		//String itemname=newBid.getItemName();
		UUID uid=UUID.randomUUID();
		//Long itemno=Long.parseLong(uid.toString());
		newBid.setItemNo(uid.toString());
		try {
			inventory.save(newBid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BiddieCustomException("save failed:you might be missing property");
		}
		result="saved";
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		result=e.getMessage();
		
	}}
	else{
		throw new BiddieCustomException("end date should be greater or same to the start auction date");
	}
	return result;
	
}
private boolean checkdates(Items newBid) throws BiddieCustomException, ParseException{
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	sdf.setTimeZone(TimeZone.getTimeZone("IST"));
	if(sdf.parse(newBid.getEndAuction()).getTime()<sdf.parse(sdf.format(new Date())).getTime() ){
		throw new BiddieCustomException("cannot save because auction end date is over");
	}
	return newBid.getStartAuction().compareTo(newBid.getEndAuction())<=0;
}
public List<Items> listItems(){
List<Items>list=(List<Items>) inventory.findAll();
return list;
}
public  List<Items>availableBid() throws java.text.ParseException{
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	sdf.setTimeZone(TimeZone.getTimeZone("IST"));
	List<Items> available=new ArrayList<>();
	System.out.println(sdf.format(new Date()));
	String todaysdate=sdf.format(new Date());
	Date indate=sdf.parse(todaysdate);
	System.out.println(indate.getTime());

	List<Items>list=inventory.findAll();
	if(list.size()>0){
		for(Items item:list){
			Date startDate=sdf.parse(item.getStartAuction()) ;
			long checkstart=startDate.getTime();
			System.out.println(checkstart);
			Date endDate=sdf.parse(item.getEndAuction()) ;
			long checkend=endDate.getTime();
			System.out.println(checkend);
			if(checkstart<=indate.getTime()&&checkend>=indate.getTime()){
				available.add(item);
			}
			
		}
		
	}
	
	
	
	//List<Items>list=inventory.availableBid(todaysdate);
	//System.out.println("endauc"+list.get(0).getEndAuction());
	//System.out.println(list);
	return available;
}
public boolean available(String itemno) throws ParseException{
	List<Items> available=availableBid();
	int count=0;
	for(Items item:available){
		if(item.getItemNo().equals(itemno)){
			count++;
		}
	}
	if(count==1){
		return true;
	}
	else
		return false;
}
public String letsBid(BidList bidList) throws BiddieCustomException, ParseException{
	System.out.println("bidlist--------"+bidList);
	String itemno=bidList.getItem_no();
	if(available(itemno)){
	if(inventory.exists(itemno)){
		if(bidrep.itemExist(itemno).size()>0){
		List<BidList> highestBid1=(List<BidList>) bidrep.findAll();
		if(highestBid1.size()>0){
			
		if(bidList.getBid()>bidrep.highestbid(itemno).get(0).getBid()){
			bidrep.save(bidList);
			return "bid successfull";
		}
		else
			throw new BiddieCustomException("Please bid higher than current bid");
		}}
		String basebid =inventory.getBaseBid(itemno);
		System.out.println("basebid"+basebid.trim());
	if(bidList.getBid()>Long.parseLong(basebid.substring(0, basebid.length()-1).trim()))	{
	bidrep.save(bidList);
	return "bid successfull";}
	else{
		throw  new BiddieCustomException("bid higher than basebid");}
	}
	else{
		throw new BiddieCustomException("the mentioned item no is not available");}}
	else{
		return "bid time over";
	}
}
public List<BidList> highestBid(String itemno){
	List<BidList> highestBid=bidrep.highestbid(itemno);
	for(BidList b:highestBid){
		System.out.println(b.toString());
	}
	return highestBid;
}

@Scheduled(fixedDelayString="${delay:10000}")
public void finishDeal() throws ParseException{
	System.out.println("************inside scheduler**************");
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	sdf.setTimeZone(TimeZone.getTimeZone("IST"));
	long todayDate=sdf.parse(sdf.format(new Date())).getTime();
	List<Items>items=inventory.findAll();
	if(items.size()>0){
		for(Items item:items){
			String endAuc=item.getEndAuction();
		long endDate=sdf.parse(endAuc).getTime();
		if(endDate<todayDate){
			String itemno = item.getItemNo();
			if(bidrep.itemExist(itemno).size()>0){
			List<BidList>highestbid=highestBid(itemno);
			//String emailid=highestbid.get(0).getEmail_id();
			sendTopBid(highestbid.get(0));
			sendowner(item,"topbid",highestbid.get(0));
			inventory.delete(item);
			}
			else{
				sendowner(item,"nobid",null);
				inventory.delete(item);
			}
		}
		
		}
	}
}
private void sendTopBid(BidList highestbid ){
	System.out.println("**************sendtopbid********************");
	emaildetails = new EmailDetails();
	emaildetails.setEmailID(highestbid.getEmail_id());
	emaildetails.setMsg("Congrats!!!You have won the item ("+highestbid.getItem_no()+" ) by bidding the amount of "+highestbid.getBid()+"$");
	String msg=new Gson().toJson(emaildetails);
	MessageProperties messageProperties = new MessageProperties();
	messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
	Message message=new Message(msg.getBytes(), messageProperties);
	System.out.println("message topbid*************"+message.toString());
	bugs.send(message);
}
private void sendowner(Items items,String why,BidList b ){
	System.out.println("**************sendowner********************");
	emaildetails = new EmailDetails();
	if(why.equals("topbid")){
		emaildetails.setEmailID(items.getEmailid());
		emaildetails.setMsg("This is to inform your item ("+items.getItemNo()+" ) has received a bid of "+b.getBid()+" from "+b.getEmail_id());
	}else if(why.equals("nobid")){
	emaildetails.setEmailID(items.getEmailid());
	emaildetails.setMsg("This is to inform your item ("+items.getItemNo()+" ) has not received any bid.try auctioning some other day");}
	String msg=new Gson().toJson(emaildetails);
	MessageProperties messageProperties = new MessageProperties();
	messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
	Message message=new Message(msg.getBytes(), messageProperties);
	System.out.println("message sendowner*************"+message.toString());
	bugs.send(message);
}
}
