package vn.viettel.browser.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import vn.viettel.browser.config.AppConfig;
import vn.viettel.browser.worker.SendMessWorker;

public class AppTest {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

	    SendMessWorker printTask1 = (SendMessWorker) context.getBean("sendMessWorker");
	    taskExecutor.execute(printTask1);

	   /* PrintTask2 printTask2 = (PrintTask2) context.getBean("printTask2");
	    printTask2.setName("Thread 2");
	    taskExecutor.execute(printTask2);

	    PrintTask2 printTask3 = (PrintTask2) context.getBean("printTask2");
	    printTask3.setName("Thread 3");
	    taskExecutor.execute(printTask3);

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
		}*/

	}

}
