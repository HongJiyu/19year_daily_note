package com.hjy.dire;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class send {
public static void main(String[] args) throws Exception {
	ConnectionFactory factory = new ConnectionFactory();
	factory.setUsername("guest");
	factory.setPassword("guest");
	factory.setPort(5672);
	factory.setHost("localhost");
	
	Connection conn=factory.newConnection();
	Channel channel = conn.createChannel();
	channel.queueDeclare("queue_direct", false, false, false, null);
		String message = String.format("发送时间：%s", new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-sss").format(new Date()));
		channel.basicPublish("direct_ex","direct_ms" , null, message.getBytes("UTF-8"));
		

	channel.close();
	conn.close();
}
}
