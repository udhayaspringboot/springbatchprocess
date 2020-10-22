package com.springbatchuser.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springbatchuser.batch.ManualItemReader;
import com.springbatchuser.batch.MtliBean;
import com.springbatchuser.config.SpringBatchConfig;
import com.springbatchuser.model.User;

import javaxt.io.Directory;
import javaxt.io.Directory.Event;

@RestController
@RequestMapping("/load")
public class LoadController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	@Autowired
	@Qualifier("batch2.job2")
	Job job2;
	private List<Resource> list;

	public static Resource[] getRes() {
		return res;
	}

	public static void setRes(Resource[] res) {
		LoadController.res = res;
	}

	/*
	 * @Autowired
	 * 
	 * @Qualifier("job1") Job job1;
	 */
	static Resource[] res;

	@RequestMapping("/home")
	public ModelAndView home() {
		return new ModelAndView("home");
	}

	@RequestMapping("/login")
	public ModelAndView logi() {
		ModelAndView mbn = new ModelAndView("home");
		mbn.addObject("logi", "login");
		return mbn;
	}

	@RequestMapping("/uploadfiles")

	public ModelAndView handleFileUpload(HttpServletRequest request,
			@RequestParam("fileUpload") MultipartFile[] fileUpload) throws Exception {
		File folderCopy = new File("F:\\afiles\\useslist");
		Path pathCopy = folderCopy.toPath();
		if (fileUpload != null && fileUpload.length > 0) {
			for (MultipartFile aFile : fileUpload) {

				System.out.println("Saving file: " + aFile.getOriginalFilename());
				byte[] bytes = aFile.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(pathCopy + File.separator + aFile.getOriginalFilename())));
				stream.write(bytes);
				stream.flush();
				stream.close();
				

			}
		}
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time3", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);
		System.out.println("parameters are " + parameters.toString());
		System.out.println("JobExecution: " + jobExecution.getStatus().toString());

		return new ModelAndView("home");

	}

	@RequestMapping("/logicheck")
	public ModelAndView logiCheck(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		ModelAndView mdb = new ModelAndView("home");
		if (userName.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {

			return new ModelAndView("redirect:/home");

		} else {
			mdb.addObject("logi", "login");
			mdb.addObject("logfail", "UserName or Password mismatch?..");

			return mdb;
		}

	}

	@GetMapping
	public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException {
		SpringBatchConfig spc = new SpringBatchConfig();
		LoadController jdcd = new LoadController();
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);
		System.out.println("parameters are " + parameters.toString());
		System.out.println("JobExecution: " + jobExecution.getStatus().toString());
		
		System.out.println("Batch is exected sccesflly...");

		

		Directory folder = new Directory("F:\\afiles");
		Directory folderCopy = new Directory("F:\\sqlfilescopy");

		try {
			sync(folder, folderCopy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (jobExecution.isRunning()) {
			System.out.println("...executes load() values");
		}

		return jobExecution.getStatus();
	}

	@RequestMapping("/manualmodelist")
	public ModelAndView manualSchedule() {
		List<String> fileNames = new ArrayList<>();

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		// Resource[] res = null;
		ModelAndView mdv = new ModelAndView("home");

		try {
			Resource[] resources = resolver.getResources("file:f:/afiles/useslist/user*.csv");

			for (Resource file : resources) {

				fileNames.add(file.getFilename());
			}
			mdv.addObject("fName", fileNames);
			mdv.addObject("manualtest", "checkmanual");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mdv;

	}

	@RequestMapping("/manualmode")
	public ModelAndView manualmodeSch(HttpServletRequest request,HttpServletResponse response)
	{
		//JobScheduleController js = new JobScheduleController();
		//List<History> lisHis = new ArrayList<>();
		
		Random rannum = new Random();
		
		String dateTimeLocal = request.getParameter("datetimeloc");
		
		String[] fileNames = request.getParameterValues("fnames");
		
		ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
      //  Resource[] res = null;
        ModelAndView mdv = new ModelAndView("home");
      res = new Resource[fileNames.length];
	
			int i=0;
				try {
					Resource[] resources = resolver.getResources("file:f:/afiles/useslist/user*.csv" );
					
					for (Resource resource : resources) {
						
						for (String resource2 : fileNames) {
							
							
							if(resource.getFilename().equalsIgnoreCase(resource2))
							{
								//list.add(res[i]);
								res[i] =resource;
								i++;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					
				}
				
				for (Resource resource : res) {
					System.out.println("resorces selected "+resource.getFilename());
				
				System.out.println("Saving file: " + resource.getFilename());
			
				
		
				}
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				
		
				
				  Map<String, JobParameter> maps = new HashMap<>(); 
				  maps.put("time9", new JobParameter(System.currentTimeMillis())); 
				  //maps.put("reso", res);
				 SpringBatchConfig mtb = new SpringBatchConfig(res);
					System.out.println("exeutes");
				  JobParameters parameters = new JobParameters(maps); 
				  try {
					JobExecution jobExecution = jobLauncher.run(job2,parameters);
				} catch (JobExecutionAlreadyRunningException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JobRestartException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JobInstanceAlreadyCompleteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JobParametersInvalidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				  
				
			}
		};
		
		
		try {
			Date futureDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateTimeLocal);
			System.out.println(futureDate);
			Timer timer = new Timer();
			timer.schedule(task, futureDate); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return mdv; 
		
	}


	private void sync(Directory source, Directory destination) throws Exception {
		// JobScheduleController js = new JobScheduleController();

		LoadController jdc = new LoadController();
		// Create an event que
		java.util.List events = source.getEvents();

		// Process events
		while (true) {

			Event event;

			// Wait for new events to be added to the que
			synchronized (events) {
				while (events.isEmpty()) {
					try {
						// js.autoSchedule();
						System.out.println("waiting to do a event");
						events.wait();
						System.out.println("events are waiting");
					} catch (InterruptedException e) {
					}
				}
				event = (Event) events.remove(0);
			}

			// Process event
			int eventID = event.getEventID();
			if (eventID == Event.DELETE) {

				// Build path to the file in the destination directory
				String path = destination + "\\" + event.getFile().substring(source.toString().length());
				System.out.println("path is " + path);
				// Delete the file/directory
				new java.io.File(path).delete();
			} else {

				// Check if the event is associated with a file or directory so
				// we can use the JavaXT classes to create or modify the target.
				java.io.File obj = new java.io.File(event.getFile());
				if (obj.isDirectory()) {
					javaxt.io.Directory dir = new javaxt.io.Directory(obj);
					javaxt.io.Directory dest = new javaxt.io.Directory(
							destination + dir.toString().substring(source.toString().length()));

					switch (eventID) {

					case (Event.CREATE):
						dir.copyTo(dest, true);
						System.out.println("event creation");
						break;
					case (Event.MODIFY):
						System.out.println("event modification");
						break; // TODO
					case (Event.RENAME): {
						javaxt.io.Directory orgDir = new javaxt.io.Directory(event.getOriginalFile());
						dest = new javaxt.io.Directory(
								destination + orgDir.toString().substring(source.toString().length()));
						dest.rename(dir.getName());
						System.out.println("renaming");
						break;
					}
					}

				} else {
					javaxt.io.File file = new javaxt.io.File(obj);
					javaxt.io.File dest = new javaxt.io.File(
							destination + file.toString().substring(source.toString().length()));

					switch (eventID) {

					case (Event.CREATE):
						event.getFile();
						System.out.println("file name is " + event.getFile());
						LoadController jdcde = new LoadController();
						Map<String, JobParameter> maps = new HashMap<>();
						maps.put("time2", new JobParameter(System.currentTimeMillis()));
						JobParameters parameters = new JobParameters(maps);
						JobExecution jobExecution = jobLauncher.run(job, parameters);

						System.out.println("JobExecution: " + jobExecution.getStatus().toString());

						System.out.println("Batch is exected sccesflly...");

						;
						SpringBatchConfig spc = new SpringBatchConfig();

						ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) spc.taskExecutor();

						// System.out.println("threds"+ );

						// ThreadPoolTaskExecutor taskExecutors = new ThreadPoolTaskExecutor();
						// taskExecutors.set
						System.out.println(
								"value job " + job.getJobParametersIncrementer() + " " + taskExecutor.getActiveCount());

						// jdc.load();//js.autoSchedule();// file.copyTo(dest, true);

						System.out.println("createed else part");
						// Thread.sleep(3000);
						break;
					case (Event.MODIFY):
						file.copyTo(dest, true);

						/*
						 * File folder = new File("F:\\sqlfiles"); File[] listOfFiles =
						 * folder.listFiles(); String st = "successorfailure";
						 * 
						 * ModelAndView mdv = new ModelAndView("home"); for (File file2 : listOfFiles) {
						 * 
						 * if(file2.getName().equals(file.getName())) { ScriptRunner scriptRunner = new
						 * ScriptRunner(js.getConnection());
						 * 
						 * try { Reader reader = new BufferedReader(new FileReader(file2));
						 * scriptRunner.runScript(reader); mdv.addObject(st,
						 * "Successfully executed for modified file");} catch (FileNotFoundException e)
						 * { // TODO Auto-generated catch block e.printStackTrace(); } }
						 * 
						 * } System.out.println("modified else part");
						 */ break;
					case (Event.RENAME): {
						javaxt.io.File orgFile = new javaxt.io.File(event.getOriginalFile());
						dest = new javaxt.io.File(
								destination + orgFile.toString().substring(source.toString().length()));
						dest.rename(file.getName());
						System.out.println("renamed else part");
						break;
					}

					}

				}
			}
		}

	}
}
