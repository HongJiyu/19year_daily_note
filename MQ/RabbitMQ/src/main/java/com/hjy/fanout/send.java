package com.hjy.fanout;

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
		final String ExchangeName = "fanout_ec"; // 交换器名称
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();
		channel.exchangeDeclare(ExchangeName, "fanout"); // 声明fanout交换器
		String message = "时间：" + new Date().getTime();
		channel.basicPublish(ExchangeName, "", null, message.getBytes("UTF-8"));
		System.out.println("消息发送出去了");
		channel.close();
		conn.close();
	}

}
