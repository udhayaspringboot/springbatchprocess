package com.springbatchuser.batch;

import com.springbatchuser.model.User;
import com.springbatchuser.repository.UserRepository;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBWriter implements ItemWriter<User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void write(List<? extends User> users) throws Exception {
    	//for (User user : users) {
    	 System.out.println("new thread before"+Thread.currentThread().getName() + " "+Thread.activeCount() +" "+Thread.currentThread().getPriority());
    		 userRepository.saveAll(users);
	//	}
    	
        System.out.println("Data Saved for Users: " + users.toString());
        System.out.println("new thread after"+Thread.currentThread().getName() + " "+Thread.activeCount() +" "+Thread.currentThread().getPriority());
       
        //userRepository.save(users);
    }
}
