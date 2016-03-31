package com.andcopro.bean;


public class StockBean {
	
	String locator = null;
	String poDocumentNo = null;
	String soDocumentNO = null;
	String quoteDocumentNo = null;
	String costcoNo = null;
	String bpName = null;
	int orderId = 0;
	int locatorId = 0;
	int	inoutId = 0;
	Locator selectedLocator = null;
	
	public StockBean(String locator, String poDocumentNo, String soDocumentNO,
			String quoteDocumentNo, String costcoNo, int orderId, int locatorId, String bpName) {
		super();
		this.locator = locator;
		this.poDocumentNo = poDocumentNo;
		this.soDocumentNO = soDocumentNO;
		this.quoteDocumentNo = quoteDocumentNo;
		this.costcoNo = costcoNo;
		this.orderId = orderId;
		this.locatorId = locatorId;
		this.bpName = bpName;
	}
	public String getLocator() {
		return locator;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}
	public String getPoDocumentNo() {
		return poDocumentNo;
	}
	public void setPoDocumentNo(String poDocumentNo) {
		this.poDocumentNo = poDocumentNo;
	}
	public String getSoDocumentNO() {
		return soDocumentNO;
	}
	public void setSoDocumentNO(String soDocumentNO) {
		this.soDocumentNO = soDocumentNO;
	}
	public String getQuoteDocumentNo() {
		return quoteDocumentNo;
	}
	public void setQuoteDocumentNo(String quoteDocumentNo) {
		this.quoteDocumentNo = quoteDocumentNo;
	}
	public String getCostcoNo() {
		return costcoNo;
	}
	public void setCostcoNo(String costcoNo) {
		this.costcoNo = costcoNo;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getLocatorId() {
		return locatorId;
	}
	public void setLocatorId(int locatorId) {
		this.locatorId = locatorId;
	}

	public int getInoutId() {
		return inoutId;
	}
	public void setInoutId(int inoutId) {
		this.inoutId = inoutId;
	}
	public Locator getSelectedLocator() {
		return selectedLocator;
	}
	public void setSelectedLocator(Locator selectedLocator) {
		this.selectedLocator = selectedLocator;
	}
	
	public String getBpName() {
		return bpName;
	}
	public void setBpName(String bpName) {
		this.bpName = bpName;
	}
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer("StockBean[id=");
		sb.append(getLocatorId()).append(", locator=").append(getLocator())
			.append(", poDocumentNo=").append(getPoDocumentNo())
			.append(", C_Order_ID=").append(getOrderId())
			.append("]");
		return sb.toString();
	}
	
	

}
