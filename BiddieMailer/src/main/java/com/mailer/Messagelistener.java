package com.mailer;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;


public class Messagelistener implements MessageListener{
	
@Autowired
RabbitTemplate rabbit;
@Autowired
SendGrid sendGrid;
@Value("${spring.rabbitmq.response.queuename:biddie.response.queue}")
private String responsequeueName;
@Value("${sendgrid_host:smtp.sendgrid.net}")
private String sendgrid_host;
@Value("${sendgrid_username:EUITqV7Hf5BZ8513}")
private String sendgrid_username;
@Value("${sendgrid_password:0pAzoa9GW8}")
private String sendgrid_password;
	@Override
	public void onMessage(Message message) {
		String messageContent = new String(message.getBody());
		System.out.println("biddiemailer"+messageContent);
		
		try {
			System.out.println("parsed content*********"+new JSONParser().parse(messageContent).toString());
			sendMail((JSONObject) new JSONParser().parse(messageContent));
			MessageProperties mp = new MessageProperties();
			mp.setContentType(MessageProperties.CONTENT_TYPE_BYTES);
			Message m= new Message("mail sent".getBytes(),mp);
			rabbit.convertAndSend(responsequeueName, m);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public void sendMail(JSONObject message) throws IOException{

	SimpleMailMessage smm=new SimpleMailMessage();
	
	smm.setFrom("biddie@gmail.com");
	smm.setTo(message.get("emailID").toString());
	smm.setSubject("biddie");
	smm.setText(message.get("msg").toString());
	System.out.println("*****************sendmailsmm"+smm.toString());
	Email from = new Email("biddie@gmail.com");
    String subject = "Biddie-cos biddin is fun";
    Email to = new Email(message.get("emailID").toString());
    Content content = new Content("text/plain", message.get("msg").toString());
	Mail mail = new Mail(from, subject, to, content);
	 Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sendGrid.api(request);
	      System.out.println("responseStatus="+response.getStatusCode());
	      System.out.println("responseBody="+response.getBody());
	      System.out.println("responseHeaders="+response.getHeaders());
	    } catch (IOException ex) {
	      throw ex;
	    }
	//mailSender.send(smm);
}
}
