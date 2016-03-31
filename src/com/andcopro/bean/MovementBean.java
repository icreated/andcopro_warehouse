/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 13 nov. 2015
 */
package com.andcopro.bean;

import java.math.BigDecimal;

import org.compiere.model.MCharge;
import org.compiere.util.KeyNamePair;

public class MovementBean {

	
	KeyNamePair product;
	Locator locator;
	Locator locatorTo;
	BigDecimal	qtyOnHand;
	BigDecimal	qty;
	MCharge charge;
	
	

	public MovementBean(KeyNamePair product, Locator locator, Locator locatorTo, BigDecimal	 qtyOnHand) {
		super();
		this.product = product;
		this.locator = locator;
		this.locatorTo = locatorTo;
		this.qtyOnHand = qtyOnHand;
	}
	
	
	


	public MovementBean(KeyNamePair product,  Locator locator, BigDecimal qtyOnHand, BigDecimal qty,
			MCharge charge) {
		super();
		this.product = product;
		this.locator = locator;
		this.qtyOnHand = qtyOnHand;
		this.qty = qty;
		this.charge = charge;
	}





	public KeyNamePair getProduct() {
		return product;
	}


	public  Locator getLocator() {
		return locator;
	}


	public Locator getLocatorTo() {
		return locatorTo;
	}


	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}


	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}





	public BigDecimal	getQty() {
		return qty;
	}


	public MCharge getCharge() {
		return charge;
	}



}
