package com.jms.topic;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Consumer implements MessageListener {

	public void onMessage(Message arg0) {

		TextMessage textMessage=(TextMessage) arg0;
		try {
			System.out.println(textMessage.getText());
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext application=new ClassPathXmlApplicationContext("consumer.xml");
	}
	

}
