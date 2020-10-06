package com.hjy.topic;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class send {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		factory.setHost("localhost");
		String routingKey = "com.mq.rabbit.error1";
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();
		channel.exchangeDeclare("topic_ex", "topic"); // 声明topic交换器
//		String message = "时间：" + new Date().getTime();
		channel.basicPublish("topic_ex", routingKey, null, routingKey.getBytes("UTF-8"));
		channel.close();
		conn.close();
	}
}
