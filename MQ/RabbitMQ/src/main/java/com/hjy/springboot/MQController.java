package com.hjy.springboot;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("mq")
@CrossOrigin(allowCredentials = "true")
public class MQController {

	
	@Autowired
	RabbitConfigUtil send;

	
	@RequestMapping("/send")
	public void send()
	{
		for(int i=0;i<2;i++)
		{
			send.sendMsg("promoId="+i);
		}
	}
	

	
}
