package com.andcopro.bean;

import java.util.Date;

public class SchedulerEvent {
	
	int id;
	int orderId;
	int resquestId;
	String title;
	String description;
    Date startDate;
    Date endDate;
    String dayDelta;
    String minuteDelta;
    String styleClass;
    String backgroundColor;
    String address;
    boolean confirmed = false;
    boolean allDay = false;
    boolean editable = true;
    int dayEvent = 0;
    int workedTime = 0;
    int freeTime = 0;
    
    
    
    
    
	public SchedulerEvent(int id, int orderId, String title, Date startDate, Date endDate) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDayDelta() {
		return dayDelta;
	}
	public void setDayDelta(String dayDelta) {
		this.dayDelta = dayDelta;
	}
	public String getMinuteDelta() {
		return minuteDelta;
	}
	public void setMinuteDelta(String minuteDelta) {
		this.minuteDelta = minuteDelta;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public boolean isConfirmed() {
		return confirmed;
	}
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public int getDayEvent() {
		return dayEvent;
	}
	public void setDayEvent(int dayEvent) {
		this.dayEvent = dayEvent;
	}
	public int getWorkedTime() {
		return workedTime;
	}
	public void setWorkedTime(int workedTime) {
		this.workedTime = workedTime;
	}
	public int getFreeTime() {
		return freeTime;
	}
	public void setFreeTime(int freeTime) {
		this.freeTime = freeTime;
	}
    
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public int getResquestId() {
		return resquestId;
	}
	public void setResquestId(int resquestId) {
		this.resquestId = resquestId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("Event[id=");
		sb.append(getId()).append(", name=").append(getTitle())
			.append(", size =").append(getWorkedTime())
			.append("]");
		return sb.toString();
	}	//	toString
    

}
