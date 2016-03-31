package com.andcopro.bean;

import java.math.BigDecimal;

import org.compiere.util.KeyNamePair;

public class ReceptionBean {

	
	 boolean  selected = false;
	 boolean isCharge = false;

	BigDecimal  qty = BigDecimal.ZERO;
	 KeyNamePair uom;
	 int locatorId = 0;
	 int orderId = 0;
	 KeyNamePair product;
	 String vendorProductNo;
	 KeyNamePair orderLine;
	 String shipmentLine;
	 String invoiceLine;
	 String description;
	 String textDispute = null;
	 BigDecimal qtyBox = BigDecimal.ONE;
	 String locator = null;
	 
	public ReceptionBean() {
		
	}
	 
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	public KeyNamePair getUom() {
		return uom;
	}
	public void setUom(KeyNamePair uom) {
		this.uom = uom;
	}
	public int getLocatorId() {
		return locatorId;
	}
	public void setLocatorId(int locatorId) {
		this.locatorId = locatorId;
	}
	public KeyNamePair getProduct() {
		return product;
	}
	public void setProduct(KeyNamePair product) {
		this.product = product;
	}
	public String getVendorProductNo() {
		return vendorProductNo;
	}
	public void setVendorProductNo(String vendorProductNo) {
		this.vendorProductNo = vendorProductNo;
	}
	public KeyNamePair getOrderLine() {
		return orderLine;
	}
	public void setOrderLine(KeyNamePair orderLine) {
		this.orderLine = orderLine;
	}
	public String getShipmentLine() {
		return shipmentLine;
	}
	public void setShipmentLine(String shipmentLine) {
		this.shipmentLine = shipmentLine;
	}
	public String getInvoiceLine() {
		return invoiceLine;
	}
	public void setInvoiceLine(String invoiceLine) {
		this.invoiceLine = invoiceLine;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	 
	 
	public BigDecimal getQtyBox() {
		return qtyBox;
	}

	public void setQtyBox(BigDecimal boxQty) {
		this.qtyBox = boxQty;
	}

	
	
	public String getTextDispute() {
		return textDispute;
	}

	public void setTextDispute(String textDispute) {
		this.textDispute = textDispute;
	}
	
	public boolean isInDispute() {
		
		return getTextDispute() != null && !getTextDispute().isEmpty();
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public boolean isCharge() {
		return isCharge;
	}

	public void setCharge(boolean isCharge) {
		this.isCharge = isCharge;
	}	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("ReceptionBean[orderLineId=");
		sb.append(getOrderLine().getKey()).append(", name=").append(product.getName()).append("]");
		return sb.toString();
	}
	
}
