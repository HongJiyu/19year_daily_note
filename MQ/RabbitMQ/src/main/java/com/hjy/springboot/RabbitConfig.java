package com.hjy.springboot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
 
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @Value("${spring.rabbitmq.host}")
    private String host;
 
    @Value("${spring.rabbitmq.port}")
    private int port;
 
    @Value("${spring.rabbitmq.username}")
    private String username;
 
    @Value("${spring.rabbitmq.password}")
    private String password;
 
 
    public static final String EXCHANGE_ORDER = "exchange_order";
    public static final String QUEUE_ORDER = "queue_order";
    public static final String ROUTINGKEY_ORDER= "order";
    
    public static final String EXCHANGE_ORDER_DLX = "exchange_order_dlx";
    public static final String QUEUE_ORDER_DLX = "queue_order_dlx";
    public static final String ROUTINGKEY_ORDER_DLX= "order_dlx";

 
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }
 
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        return template;
//    }
    
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, EXCHANGE_ORDER_DLX, ROUTINGKEY_ORDER_DLX);
//    	return new RejectAndDontRequeueRecoverer();
    }
    

    @Bean
    public DirectExchange orderDLXExchange() {
        return new DirectExchange(EXCHANGE_ORDER_DLX);
    }

    @Bean
    public Queue orderDLXQueue() {
        return new Queue(QUEUE_ORDER_DLX, true); 
    }
    
    @Bean
    public Binding orderDLXBinding() {
        return BindingBuilder.bind(orderDLXQueue()).to(orderDLXExchange()).with(RabbitConfig.ROUTINGKEY_ORDER_DLX);
    }
    
    
    
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_ORDER);
    }

    @Bean
    public Queue orderQueue() {
        Map<String,Object> arguments = new HashMap<>(2);
        // 绑定该队列到私信交换机
        arguments.put("x-dead-letter-exchange",EXCHANGE_ORDER_DLX);
        arguments.put("x-dead-letter-routing-key",ROUTINGKEY_ORDER_DLX);
         return new Queue(QUEUE_ORDER,true,false,false,arguments);
    }
    
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(RabbitConfig.ROUTINGKEY_ORDER);
    }

}