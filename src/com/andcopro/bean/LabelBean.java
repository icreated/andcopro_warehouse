package com.andcopro.bean;

import java.math.BigDecimal;

import org.compiere.util.Env;

public class LabelBean {

	int id = 0;
	String value = null;
	String name = null;
	String description = null;
	int imageID = 0;
	BigDecimal price = Env.ZERO;
	
	
	
	
	public LabelBean(int id, String value, String name, String description, int imageID, BigDecimal price) {
		super();
		this.id = id;
		this.value = value;
		this.name = name;
		this.description = description;
		this.imageID = imageID;
		this.price = price;
	}
	
	public int getId() {
		return id;
	}
	public String getValue() {
		return value;
	}	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public int getImageID() {
		return imageID;
	}
	public BigDecimal getPrice() {
		return price;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Label["+getName()+"]";
	}
	
	
	
}
