package com.andcopro.bean;

public class JsonAction {
	
	String action;
	SchedulerEvent event;
	int resourceId = 0;
	
	
	
	
	
	public JsonAction(String action, SchedulerEvent object) {
		super();
		this.action = action;
		this.event = object;
	}
	
	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public SchedulerEvent getEvent() {
		return event;
	}
	public void setEvent(SchedulerEvent object) {
		this.event = object;
	}
	public int getResourceId() {
		return resourceId;
	}
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	
	

}
