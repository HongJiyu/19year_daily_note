package com.hjy.springboot;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfigUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入

	@Autowired
	private RabbitTemplate rabbitTemplate;

	// 
	RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		public void confirm(CorrelationData correlationData, boolean isack, String cause) {
			if (isack == false) {
				System.out.println("消息拒绝接收的原因是:" + cause);
			} else {
				// 处理报错也走这里？？
				System.out.println("消息发送成功到（RabbitMQ服务，有的说是到交换机），但不能保证发送到了队列，因为发送到交换机就返回true了");
			}
		}
	};


	RabbitTemplate.ReturnCallback returnCallback = new ReturnCallback() {

		@Override
		public void returnedMessage(Message message, int replyCode, String desc, String exchangeName, String routeKey) {
			System.out.println("err code :" + replyCode);
			System.out.println("错误消息的描述 :" + desc);
			System.out.println("错误的交换机是 :" + exchangeName);
			System.out.println("错误的路由键是 :" + routeKey);
			System.out.println("消息发送到交换机，但是没发送到队列就会回调");

		}
	};

	public void sendMsg(String content) {
		CorrelationData cData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_ORDER, RabbitConfig.ROUTINGKEY_ORDER ,content, message -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//			message.getMessageProperties().setExpiration("5000");
			logger.info("消息发送成功");
			return message;
		}, cData);

	}

}