package com.springbatchuser.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.springbatchuser.model.User;
import com.springbatchuser.repository.UserRepository;
@Component
public class ManualDbWriter implements ItemWriter<User> {
	
	   @Autowired
	    private UserRepository userRepository;

	@Override
	public void write(List<? extends User> usersManal) throws Exception {
		// TODO Auto-generated method stub
		
		 System.out.println("new thread before"+Thread.currentThread().getName() + " "+Thread.activeCount() +" "+Thread.currentThread().getPriority());
		 userRepository.saveAll(usersManal);
//	}
	
    System.out.println("Data Saved for Users: " + usersManal.toString());
    System.out.println("new thread after"+Thread.currentThread().getName() + " "+Thread.activeCount() +" "+Thread.currentThread().getPriority());
   
    //userRepository.save(users);
}

}