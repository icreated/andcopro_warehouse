package com.andcopro.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.andcopro.bean.SchedulerEvent;

public class SchedulerModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<SchedulerEvent> events;

	public SchedulerModel() {
		events = new ArrayList<SchedulerEvent>(); 
	}
	
	public SchedulerModel(List<SchedulerEvent> events) {
		this.events = events;
	}
	
	public void addEvent(SchedulerEvent event) {
		
		events.add(event);
	}
	
	public boolean deleteEvent(SchedulerEvent event) {
		return events.remove(event);
	}
	
	public List<SchedulerEvent> getEvents() {
		return events;
	}
	
	public SchedulerEvent getEvent(int id) {
		for(SchedulerEvent event : events) {
			if(event.getId() ==id)
				return event;
		}
		
		return null;
	}
	
	public void updateEvent(SchedulerEvent event) {
		int index = -1;
		
		for(int i = 0 ; i < events.size(); i++) {
			if(events.get(i).getId() == event.getId()) {
				index = i;
				
				break;
			}
		}
		
		if(index >= 0) {
			events.set(index, event);
		}
	}
	
	public int getEventCount() {
		return events.size();
	}

	public void clear() {
		events = new ArrayList<SchedulerEvent>();
	}
}