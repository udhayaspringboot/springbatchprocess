package com.springbatchuser.model;

import org.springframework.core.io.Resource;

public class ResourcesOne {
	
	private Resource[] resour;

	public Resource[] getResour() {
		return resour;
	}

	public void setResour(Resource[] resour) {
		this.resour = resour;
	}

	public ResourcesOne(Resource[] resour) {
		
		this.resour = resour;
	}
	

}
