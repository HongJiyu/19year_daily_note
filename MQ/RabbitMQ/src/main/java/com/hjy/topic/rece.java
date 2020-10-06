package com.hjy.topic;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class rece {
public static void main(String[] args) throws Exception {
	ConnectionFactory factory = new ConnectionFactory();
	factory.setUsername("guest");
	factory.setPassword("guest");
	factory.setPort(5672);
	factory.setHost("localhost");
	Connection conn = factory.newConnection();
	Channel channel = conn.createChannel();
	channel.exchangeDeclare("topic_ex", "topic"); // 声明topic交换器
	String queueName = channel.queueDeclare().getQueue(); // 声明队列
	String bindingKey = "#.error";
	channel.queueBind(queueName, "topic_ex", bindingKey);
	Consumer consumer = new DefaultConsumer(channel) {
	    @Override
	    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
	            byte[] body) throws IOException {
	        String message = new String(body, "UTF-8");
	        System.out.println(bindingKey + "|接收消息 => " + message);
	    }
	};
	channel.basicConsume(queueName, true, consumer);
}
}
