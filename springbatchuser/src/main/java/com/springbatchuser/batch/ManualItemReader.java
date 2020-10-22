package com.springbatchuser.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.springbatchuser.controller.LoadController;
import com.springbatchuser.model.User;
@Component
public class ManualItemReader implements ItemReader<User> {

	static Resource[] rdf;
	
	
	/*
	 * public ManualItemReader() { initialize();
	 * 
	 * }
	 */
	public void setResour(Resource[] resour) {
		System.out.println("setres meth callede");
		
		for (Resource resource : rdf) {
			System.out.println("file name in setter"+resource.getFilename());
		}
		
	}
	@Override
	public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		// TODO Auto-generated method stub
		//Resource[] rdff = getResour();
		/*
		 * LoadController lcv = new LoadController(); Resource[] rdfg = lcv.getRes();
		 */
		MultiResourceItemReader<User> resourceItemReader3 = new MultiResourceItemReader<User>();
		resourceItemReader3.setResources(rdf);
		
		for (Resource resource : rdf) {
			
			System.out.println("values"+resource.getFilename());
		}
		User use = new User(1,"hai","dep",16532);
		
		return (User) resourceItemReader3.getCurrentResource();
	}
	
	public FlatFileItemReader<User> reader2() {
		// Create reader instance
		FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
		// Set number of lines to skips. Use it if file has header rows.
		System.out.println("get file names in flat file " + reader.toString());
		reader.setLinesToSkip(1);
		System.out.println(reader.toString());
		// Configure how each line will be parsed and mapped to different values
		reader.setLineMapper(new DefaultLineMapper<User>() {
			{
				// 3 columns in each row
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "name", "dept", "salary" });
					}
				});
				// Set values in User class
				setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {
					{
						setTargetType(User.class);
					}
				});
			}
		});
		// System.out.println("reader is "+reader.toString());
		// reader.open(new ExecutionContext());
		return reader;
	}

	


}
