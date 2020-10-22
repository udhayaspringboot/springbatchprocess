package com.springbatchuser.batch;

import java.io.IOException;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.springbatchuser.config.SpringBatchConfig;
import com.springbatchuser.controller.LoadController;
import com.springbatchuser.model.User;
@Component
public class MtliBean {
	
	//static Resource[] rdf;
	@Value("file:f:\\sqlfilescopy\\useslist\\usersample.csv")
	 Resource[] resources;
	
	

	   
	  
	   

	   public MtliBean(Resource[] resources) {
		//super();
		this.resources = resources;
	}

	public MtliBean() {
		   
		   
		
		}


	// @StepScope
	    public FlatFileItemReader<User> reader1() 
	    {
	        // Create reader instance
	        FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
	        // Set number of lines to skips. Use it if file has header rows.
	        System.out.println("get file names in flat file "+reader.toString());
	        reader.setLinesToSkip(1);
	        System.out.println(reader.toString());
	        // Configure how each line will be parsed and mapped to different values
	        reader.setLineMapper(new DefaultLineMapper<User>() {
	            {
	                // 3 columns in each row
	                setLineTokenizer(new DelimitedLineTokenizer() {
	                    {
	                        setNames(new String[] { "id","name","dept","salary" });
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
	
//@StepScope

	    public MultiResourceItemReader<User> multiResourceItemReader3() {
	    	
	    	// if(LoadController.getRes() !=null) {
	    	//Resource[] ref = LoadController.getRes().;
	    	MultiResourceItemReader<User> resourceItemReader3 = new MultiResourceItemReader<User>();
		/*
		 * LoadController lcv = new LoadController(); Resource[] rdf = lcv.getRes();
		 */
	    	
	    	for (Resource resource : resources) {
				System.out.println("resource name "+resource.getFilename());
			}
	    	//resources = spcv.getRes1();
	    	//ClassLoader cl = this.getClass().getClassLoader();
			//ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
			//Resource[] resources = null;
			/*try {
				//resources = resolver.getResources("file:f:/sqlfilescopy/*.csv");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
							
						//	System.out.println("resource in multi item reader "+resources[0].getFilename());

					/*
					 * try { resources = resolver.getResources("file:f:/afiles/useslist/user*.csv");
					 * } catch (IOException e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 */
	    	System.out.println("executes multi item reader");
				    	resourceItemReader3.setResources(resources);
						resourceItemReader3.setDelegate(reader1());
						
						
			
			return resourceItemReader3;
			
			
			// Resource[] res = null;
	    	// }else
	    	 //{
			//return null;
	    	// }
	    }


}
