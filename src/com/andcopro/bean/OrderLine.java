package com.andcopro.bean;

import java.math.BigDecimal;

public class OrderLine {
	
	int id = 0;
	String name = null;
	String description = null;
	BigDecimal qtyOrdered;
	BigDecimal qtyOnHand;
	
	String floorplan;
	BigDecimal window_number;
	BigDecimal width;
	BigDecimal length;
	
	
	
	
	
	
	public OrderLine(int id, String name, String description, BigDecimal qtyOrdered, BigDecimal qtyOnHand) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.qtyOrdered = qtyOrdered;
		this.qtyOnHand = qtyOnHand;
	}
	
	
	
	
	public OrderLine(int id, String name, String description, BigDecimal qtyOrdered, BigDecimal qtyOnHand,
			String floorplan, BigDecimal window_number, BigDecimal width, BigDecimal length) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.qtyOrdered = qtyOrdered;
		this.qtyOnHand = qtyOnHand;
		this.floorplan = floorplan;
		this.window_number = window_number;
		this.width = width;
		this.length = length;
	}




	public String getFloorPlan() {
		return floorplan;
	}




	public BigDecimal getWindowNumber() {
		return window_number;
	}




	public BigDecimal getWidth() {
		return width;
	}




	public BigDecimal getLength() {
		return length;
	}




	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}
	public BigDecimal getQtyOrdered() {
		return qtyOrdered;
	}
	
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer("OrderLine[").append(getName())
			.append(", id=").append(getId())
			.append("]");
		return sb.toString();
	}
	
	

}
