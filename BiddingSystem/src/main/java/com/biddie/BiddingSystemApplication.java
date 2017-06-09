package com.biddie;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;



@EnableScheduling
@SpringBootApplication
public class BiddingSystemApplication //extends AbstractCloudConfig
{
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

	@Value("${spring.rabbitmq.exchange:biddie.exchange}")
	private String exchange;

	@Value("${deployloc:local}")
	private String deployLocation;
	@Value("${rabbit_service_name:rabbitmq}")
	private String rabbitservice;
	
	public static void main(String[] args) {
		SpringApplication.run(BiddingSystemApplication.class, args);
	}
//@Bean
public ConnectionFactory conFac(){
	//System.out.println("deployy loccc"+deployLocation);
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
	rabbitTemplate.setRoutingKey(queueName);
	return rabbitTemplate;
}
/*@Bean
public MessageConverter jsonMessageConverter() {
	JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();
	return jsonMessageConverter;
}*/
@Bean
Queue queue(){
	return new Queue(queueName);
}
@Bean
Queue queue1(){
	return new Queue(responsequeueName);
}
@Bean
DirectExchange exchange(){
	return new DirectExchange(exchange);
}
@Bean
Binding binding(Queue queue,DirectExchange exchange){
	return  BindingBuilder.bind(queue).to(exchange).with(queueName);
}
@Bean
SimpleMessageListenerContainer messagelistener(){
	SimpleMessageListenerContainer sm= new SimpleMessageListenerContainer(conFac());
	sm.setMessageListener(ml());
	sm.addQueueNames(responsequeueName);
	return sm;
}
@Bean
public ML ml(){
	return new ML();
}
}
