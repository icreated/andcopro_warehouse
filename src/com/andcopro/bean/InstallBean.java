package com.andcopro.bean;

import java.util.Date;
import java.util.List;

public class InstallBean {
	
	int orderId;
	int porderId;
	int shipmentId = 0;
	int requestId = 0;
	int eventId = 0;
	String bpName;
	String phone;
	String address1;
	String address2;
	String city;
	String postal;
	String country;
	String summary;
	String assignedName;
	String statusStyle;
	boolean isInStock = false;
	List<ResourceBean> scheduleList = null;
	boolean selected = false;
	
	String orderDocumentNo;
	String shipDocumentNo;
	String reqDocumentNo;
	String poReference;
	String poDocumentNo;
	Date dateFrom;
	Date dateTo;
	String name;
	String statusName;
	String docStatus;
	String region;
	
	

	public InstallBean(int orderId, String orderDocumentNo, String bpName, String phone, String address1,
			String address2, String city, String postal, String country, String summary, String assignedName,
			String statusStyle, boolean isInStock, List<ResourceBean> scheduleList, int requestId, String region, 
			int shipmentId, String reqDocumentNo, String poReference, String poDocumentNo, int porderId) {
		super();
		this.orderId = orderId;
		this.orderDocumentNo = orderDocumentNo;
		this.bpName = bpName;
		this.phone = phone;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.postal = postal;
		this.country = country;
		this.summary = summary;
		this.assignedName = assignedName;
		this.statusStyle = statusStyle;
		this.isInStock = isInStock;
		this.scheduleList = scheduleList;
		this.requestId = requestId;
		this.region = region;
		this.shipmentId = shipmentId;
		this.reqDocumentNo = reqDocumentNo;
		this.poReference = poReference;
		this.poDocumentNo = poDocumentNo;
		this.porderId = porderId;
	}





	public InstallBean(int shipmentId, String orderDocumentNo, String shipDocumentNo, String reqDocumentNo, String name, Date dateFrom,
			Date dateTo, String statusName, String docStatus, int requestId, int eventId, String poReference) {
		super();
		this.shipmentId = shipmentId;
		this.orderDocumentNo = orderDocumentNo;
		this.shipDocumentNo = shipDocumentNo;
		this.reqDocumentNo = reqDocumentNo;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.name = name;
		this.statusName = statusName;
		this.docStatus = docStatus;
		this.requestId = requestId;
		this.eventId = eventId;
		this.poReference = poReference;
	}


	
	public InstallBean(int R_Request_ID, String summary, String bpName, String phone, String address1, String address2, String city, String postal, String countryCode, 
			String orderDocumentNo, int C_Order_ID, String reqDocumentNo) {
		
		this.requestId = R_Request_ID;
		this.summary = summary;
		this.bpName = bpName;
		this.phone = phone;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.postal = postal;
		this.country = countryCode;
		this.orderDocumentNo = orderDocumentNo;
		this.orderId = C_Order_ID;
		this.reqDocumentNo = reqDocumentNo;
		
		
		
	}



	public int getOrderId() {
		return orderId;
	}

	public String getOrderDocumentNo() {
		return orderDocumentNo;
	}

	public String getBpName() {
		return bpName;
	}


	public String getPhone() {
		return phone;
	}


	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getPostal() {
		return postal;
	}

	public String getCountry() {
		return country;
	}

	public String getSummary() {
		return summary;
	}

	public String getAssignedName() {
		return assignedName;
	}

	public String getStatusStyle() {
		return statusStyle;
	}

	public List<ResourceBean> getScheduleList() {
		return scheduleList;
	}

	public void setScheduleList(List<ResourceBean> scheduleList) {
		this.scheduleList = scheduleList;
	}

	public boolean isInStock() {
		return isInStock;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int getRequestId() {
		return requestId;
	}

	public String getShipDocumentNo() {
		return shipDocumentNo;
	}

	public String getReqDocumentNo() {
		return reqDocumentNo;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public String getName() {
		return name;
	}

	public String getStatusName() {
		return statusName;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public int getShipmentId() {
		return shipmentId;
	}

	public int getEventId() {
		return eventId;
	}

	public String getPoDocumentNo() {
		return poDocumentNo;
	}





	public String getRegion() {
		return region;
	}

	public String getAddress() {
		
		return getAddress1()+", "+getCity()+", "+getRegion()+" "+getPostal();
	}


	public String getPoReference() {
		return poReference;
	}





	public int getPorderId() {
		return porderId;
	}





	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer("InstallBean[bp=").append(getBpName())
			.append(", documentNo=").append(getOrderDocumentNo())
			.append(", C_Order_ID=").append(getOrderId())
			.append("]");
		return sb.toString();
	}

	
}
