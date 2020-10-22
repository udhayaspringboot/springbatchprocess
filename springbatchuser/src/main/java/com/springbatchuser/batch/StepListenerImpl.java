package com.springbatchuser.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.StepListener;
import org.springframework.stereotype.Component;
@Component
public class StepListenerImpl implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("execting step efore");
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("executing afeter");
		return ExitStatus.COMPLETED;
	}
	
	

}
