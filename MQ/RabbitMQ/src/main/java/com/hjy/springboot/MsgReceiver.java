package com.hjy.springboot;



import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;


@Component
//监听消息队列A
@RabbitListener(queues = RabbitConfig.QUEUE_ORDER)
public class MsgReceiver {
 
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @RabbitHandler
    public void process1(String content,Channel channel,@Headers Map<String, Object> headers) {
            logger.info("订单队列接收处理队列A当中的消息： " + content);
            Long tag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
            int i=1/0;
            try {
				channel.basicNack(tag, false, false);
			} catch (IOException e) {
				e.printStackTrace();
			}

}
}