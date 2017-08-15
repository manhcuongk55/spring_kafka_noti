package vn.viettel.browser.worker;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope("prototype")
public class SetListFirsebaseIdToQueue implements Runnable{

	public static Queue<String> namesQueue = new LinkedList<>();

	@Override
	public void run() {
        for(int i = 0; i < 1000 ;i ++ ){
        	namesQueue.add("c7nWUG1ge-Q:APA91bEh9v47h6ZGXz40cCjv8bPQqDyYZlF4VuVf4Pz3rl3XFZr5-4MO0Wumu3upvb-TiHYxo3WD37XsDNLjSQ5HaMSYlPTynyPHcKgHZN_w8GWb_xiDIcZ6CJNy5sgtQHvG40G5IHxu");
        }
		System.out.println("Size Queue "+ namesQueue.size());
	}

}
