import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jms.queue.IProducer;


public class goToTest {

	ClassPathXmlApplicationContext application;

	@Test
	public void Producer()
	{
		application=new ClassPathXmlApplicationContext("producer.xml");
		IProducer producer=application.getBean(IProducer.class);
		for (int i=0;i<100;i++)
		{
			producer.sendMessage("发送消息"+i);
		}
		application.close();
	}
	

}
