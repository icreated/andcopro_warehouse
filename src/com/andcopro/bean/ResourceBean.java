package com.andcopro.bean;

import java.util.Date;

public class ResourceBean {
	
	Date date;
	String name;
	
	
	
	public ResourceBean(Date date, String name) {
		super();
		this.date = date;
		this.name = name;
	}
	
	public Date getDate() {
		return date;
	}
	public String getName() {
		return name;
	}
	
	

}
