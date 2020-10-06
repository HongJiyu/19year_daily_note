package com.jms.topic;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class ProducerImpl implements IProducer {
	
	@Autowired
	JmsTemplate jmsTemplate;
	@Autowired
	@Qualifier(value="topicDestination")
	Destination destination;
	public void sendMessage(final String message) {

		jmsTemplate.send(destination,new MessageCreator() {		
			public Message createMessage(Session arg0) throws JMSException {
				TextMessage textMessage=arg0.createTextMessage(message);
				System.out.println(message);
				return textMessage;
			}
		});
	}
	
	
}
