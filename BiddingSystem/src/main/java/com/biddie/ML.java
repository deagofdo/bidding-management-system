package com.biddie;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class ML implements MessageListener{

	@Override
	public void onMessage(Message message) {
		String messageContent = new String(message.getBody());
		System.out.println(messageContent);
		
	}

}
