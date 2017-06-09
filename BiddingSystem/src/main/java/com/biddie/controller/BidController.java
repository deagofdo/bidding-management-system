package com.biddie.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.biddie.BiddieCustomException;
import com.biddie.entity.BidList;
import com.biddie.entity.Items;
import com.biddie.service.BidService;

@RestController
public class BidController {
	@Autowired
	private BidService bidService;
@RequestMapping(value="/addBid",method=RequestMethod.POST)
private String addBid(@RequestBody Items newBid) throws ParseException{
	String result;
	try {
		System.out.println("email----"+newBid.getEmailid());
		result = bidService.addBid(newBid);
		return result;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		return e.getMessage();
	}

}
@RequestMapping(value="/listitems",method=RequestMethod.GET)
private List<Items> listItems(){
	List<Items> list=bidService.listItems();
	return list;
}

@RequestMapping(value="/letsbid",method=RequestMethod.POST)
private String LetsBid(@RequestBody BidList bidList) throws BiddieCustomException, java.text.ParseException{
	
	return bidService.letsBid(bidList);
}
@GetMapping("/availablebid")
private List<Items> availableBid() throws java.text.ParseException{
	List<Items> result=bidService.availableBid();
	return result;
}
@GetMapping("/highestbid/{itemno}")
private List<BidList> highestBid(@PathVariable("itemno")String itemno){
	List<BidList> highestbid=bidService.highestBid(itemno);
	return highestbid;
}
@Autowired
RabbitTemplate rabbitTemplate;
@PostMapping("/s")
private void s(@RequestBody JSONObject s){
	MessageProperties messageProperties = new MessageProperties();
	messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
	//messageProperties.set
	Message m = new Message(s.toString().getBytes(), messageProperties);
	//System.out.println("exchange name"+rabbitTemplate.getExchange());
	//rabbitTemplate.convertAndSend(rabbitTemplate.getRoutingKey(),m);
	rabbitTemplate.send(m);
}
}
