package com.springbatchuser.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.springbatchuser.batch.DBWriter;
import com.springbatchuser.batch.ManualDbWriter;
import com.springbatchuser.batch.ManualItemReader;
import com.springbatchuser.batch.ManualitemProcessor;
import com.springbatchuser.batch.MtliBean;
import com.springbatchuser.batch.Processor;
import com.springbatchuser.controller.LoadController;
import com.springbatchuser.model.User;

@Configuration
@EnableBatchProcessing

public class SpringBatchConfig {

	@Autowired
	private DBWriter dbWriter;
	@Autowired
	private Processor processor;

	@Autowired
	private ManualItemReader mReader;
	@Autowired
	private ManualDbWriter msdWrt;

	@Autowired
	private ManualitemProcessor mProc;
	

	

	@Autowired
	MtliBean mBean;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Value("file:f:/afiles/useslist/user*.csv")
	private Resource[] inputResources;
	
	
	
	
	
	static Resource[] resources;
	
	
	
	
	public SpringBatchConfig(Resource[] resources) {
		super();
		this.resources = resources;
		System.out.println("res in constructor "+ resources[0].getFilename());
		getResources();}
	
	

	public Resource[] getResources() {
		
		System.out.println("res in getter " + resources[0].getFilename());
		return resources;
	}



	public void setResources(Resource[] resources) {
		this.resources = resources;
	}



	public SpringBatchConfig() {
		super();
	}



	
	

	@Bean
	
	public Job job() {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer()).start(step()).build();
	}

	@Bean(name = "batch2.job2")
	
	public Job job2() {
		return jobBuilderFactory.get("batch2.job2").start(step1()).build();
	}
	

	@Bean

	public Step step() {
		return stepBuilderFactory.get("step").<User, User>chunk((3))

				.reader(multiResourceItemReader2())

				.processor(processor)

				.writer(dbWriter).taskExecutor(taskExecutor()).build();
	}

	

	@Bean
	//@StepScope
	public Step step1() {
		return stepBuilderFactory.get("step1").<User, User>chunk(3)
				.reader(multiResourceItemReader3())
				.processor(mProc).writer(msdWrt).taskExecutor(taskExecutor2())
				.build();
	}

	@Bean
	@Primary
	@StepScope
	public FlatFileItemReader<User> reader() {
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

	
	@Bean
	@Qualifier
	@StepScope
	public MultiResourceItemReader<User> multiResourceItemReader2() {
		MultiResourceItemReader<User> resourceItemReader = new MultiResourceItemReader<User>();

		// System.out.println(jobPara.getValue());

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		// Resource[] res = null;

		try {
			Resource[] resources = resolver.getResources("file:f:/afiles/useslist/user*.csv");
			for (Resource resource : resources) {
				System.out.println("resource names FIRST" + resource.getFilename());
				
			}
			resourceItemReader.setResources(resources);
			resourceItemReader.setDelegate(reader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("new file " + resourceItemReader.getCurrentResource());

		return resourceItemReader;
	}
@Bean
@StepScope
@Qualifier
	 public MultiResourceItemReader<User> multiResourceItemReader3() {
	    	
	    	// if(LoadController.getRes() !=null) {
	    	//Resource[] ref = LoadController.getRes().;
	    	MultiResourceItemReader<User> resourceItemReader3 = new MultiResourceItemReader<User>();
		/*
		 * LoadController lcv = new LoadController(); Resource[] rdf = lcv.getRes();
		 */
	    	SpringBatchConfig spd = new SpringBatchConfig();
	    	resources = spd.getResources();
	    	for (Resource resource : resources) {
				System.out.println("resource name "+resource.getFilename());
			}
System.out.println("executes multi item reader");
				    	resourceItemReader3.setResources(resources);
						resourceItemReader3.setDelegate(reader1());
						
						
			
			return resourceItemReader3;
			
}


@Bean
@Qualifier
@StepScope
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
	       
	        return reader;
	    }
	
	
	@Bean
	public TaskExecutor taskExecutor() {
		
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.afterPropertiesSet();
		
		return taskExecutor;
	}

	@Bean
	@StepScope
	public TaskExecutor taskExecutor2() {
		
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(20);

		taskExecutor.afterPropertiesSet();
		taskExecutor.getActiveCount();

		return taskExecutor;
	}

	

}
