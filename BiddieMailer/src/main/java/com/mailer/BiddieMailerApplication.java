package com.mailer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testng.reporters.EmailableReporter;

import com.sendgrid.*;

@SpringBootApplication
public class BiddieMailerApplication extends AbstractCloudConfig {
	@Value("${spring.rabbitmq.host:localhost}")
	private String rabbitMQHost;

	@Value("${spring.rabbitmq.port:5672}")
	private int rabbitMQPort;

	@Value("${spring.rabbitmq.username:guest}")
	private String rabbitMQUserName;

	@Value("${spring.rabbitmq.password:guest}")
	private String rabbitMQPassword;

	@Value("${spring.rabbitmq.queuename:biddie.queue}")
	private String queueName;

	@Value("${spring.rabbitmq.response.queuename:biddie.response.queue}")
	private String responsequeueName;

	@Value("${spring.rabbitmq.exchange:biddie.response.exchange}")
	private String exchange;
	@Value("${sendgrid_host:smtp.sendgrid.net}")
	private String sendgrid_host;
	@Value("${sendgrid_username:EUITqV7Hf5BZ8513}")
	private String sendgrid_username;
	@Value("${sendgrid_password:0pAzoa9GW8}")
	private String sendgrid_password;

	@Value("${deployloc:local}")
	private String deployLocation;
	@Value("${rabbit_service_name:rabbitmq}")
	private String rabbitservice;
	public static void main(String[] args) {
		SpringApplication.run(BiddieMailerApplication.class, args);
	}
	
	//@Bean
	public ConnectionFactory conFac() {
		ConnectionFactory conFac=null;
		if(deployLocation.equalsIgnoreCase("cloud")){
			CloudFactory factory = new CloudFactory();
			Cloud cloud=factory.getCloud();
			conFac=cloud.getServiceConnector(rabbitservice, ConnectionFactory.class, null);
		}
		else{
			CachingConnectionFactory ccf=new CachingConnectionFactory(rabbitMQHost);
			ccf.setPort(rabbitMQPort);
			ccf.setUsername(rabbitMQUserName);;
			ccf.setPassword(rabbitMQPassword);
			conFac= ccf;
		}
		return conFac;
	}
	@Bean
	RabbitTemplate rabbitTemplate(){
		RabbitTemplate rabbitTemplate=new RabbitTemplate(conFac());
		rabbitTemplate.setQueue(responsequeueName);
		rabbitTemplate.setRoutingKey(responsequeueName);
		return rabbitTemplate;
	}
	/*@Bean
	public MessageConverter jsonMessageConverter() {
		JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();
		return jsonMessageConverter;
	}*/
	@Bean
	Queue queue(){
		return new Queue(responsequeueName);
	}
	@Bean
	Queue queue1(){
		return new Queue(queueName);
	}
	@Bean
	DirectExchange exchange(){
		return new DirectExchange(exchange);
	}
	@Bean
	Binding binding(Queue queue,DirectExchange exchange){
		return  BindingBuilder.bind(queue).to(exchange).with(responsequeueName);
	}
	@Bean
	SimpleMessageListenerContainer messagelistener(){
		SimpleMessageListenerContainer sm= new SimpleMessageListenerContainer(conFac());
		sm.setMessageListener(ml());
		sm.addQueueNames(queueName);
		return sm;
	}
	@Bean
	Messagelistener ml(){
		return new Messagelistener();
	}
	@Bean
	public SendGrid sendGrid(){
		SendGrid sg =new SendGrid("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");
		/*JavaMailSenderImpl mailsender= new JavaMailSenderImpl();
		mailsender.setHost(sendgrid_host);
		mailsender.setUsername("username---"+sendgrid_username);
		mailsender.setPassword("password----"+sendgrid_password);
		return mailsender;*/return sg;
	}
}
