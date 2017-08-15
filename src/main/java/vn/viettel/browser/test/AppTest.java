package vn.viettel.browser.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import vn.viettel.browser.config.AppConfig;
import vn.viettel.browser.worker.SendMessWorker;
import vn.viettel.browser.worker.SetListFirsebaseIdToQueue;

public class AppTest {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
        
	    SetListFirsebaseIdToQueue setListFirsebaseIdToQueue = (SetListFirsebaseIdToQueue) context.getBean("setListFirsebaseIdToQueue");
	    taskExecutor.execute(setListFirsebaseIdToQueue);
	    
	    SendMessWorker sendMessWorker1 = (SendMessWorker) context.getBean("sendMessWorker");
	    sendMessWorker1.setName("sendMessWorker1");
	    taskExecutor.execute(sendMessWorker1);
	    
	    
	    SendMessWorker sendMessWorker2 = (SendMessWorker) context.getBean("sendMessWorker");
	    sendMessWorker2.setName("sendMessWorker2");
	    taskExecutor.execute(sendMessWorker2);
	    
	    SendMessWorker sendMessWorker3 = (SendMessWorker) context.getBean("sendMessWorker");
	    sendMessWorker3.setName("sendMessWorker3");
	    taskExecutor.execute(sendMessWorker3);

	   
		for (;;) {
			int count = taskExecutor.getActiveCount();
			System.out.println("Active Threads : " + count);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count == 0) {
				taskExecutor.shutdown();
				break;
			}
		}

	}

}
