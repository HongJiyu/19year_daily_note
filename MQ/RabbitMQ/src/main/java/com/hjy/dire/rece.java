package com.hjy.dire;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

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
	
	Connection conn=factory.newConnection();
	Channel channel = conn.createChannel();
	channel.queueDeclare("queue_direct", false, false, false, null);
	channel.exchangeDeclare("direct_ex", "direct");
	channel.queueBind("queue_direct","direct_ex", "direct_ms");
	Consumer defaultConsumer = new DefaultConsumer(channel) {
	    @Override
	    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
	            byte[] body) throws IOException {
	        String message = new String(body, "utf-8"); // 消息正文
	        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-sss").format(new Date())+"收到消息=> " + message);
	        channel.basicAck(envelope.getDeliveryTag(), false); 
	        // 手动确认消息【参数说明：参数一：该消息的index；参数二：是否批量应答，true批量确认小于当前id的消息】
	    }
	};
	channel.basicConsume("queue_direct", false, "", defaultConsumer);
	
}
}
