/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 26 nov. 2015
 */
package com.andcopro.bean;

public class Order {
	
	
	int id;
	String documentNo;
	String poReference;
	
	
	
	public Order(int id, String documentNo, String poReference) {
		super();
		this.id = id;
		this.documentNo = documentNo;
		this.poReference = poReference;
	}
	public int getId() {
		return id;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public String getPoReference() {
		return poReference;
	}
	

}
