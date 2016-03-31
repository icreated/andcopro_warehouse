/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.compiere.model.MBPartner;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRefList;
import org.compiere.model.MRequest;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Trx;
import org.jfree.util.Log;

import com.andcopro.bean.Locator;
import com.andcopro.bean.ReceptionBean;
import com.andcopro.service.InstallationService;
import com.andcopro.service.ReceptionService;
import com.andcopro.service.ServiceFactory;
import com.andcopro.util.Envs;
import com.andcopro.util.FacesUtil;

/**
 *  Reception View.
 *  @author     Sergey Polyarus
 *  @version 1.0
 *  @since   2015-10-25
 */

@ManagedBean
@ViewScoped
public class Reception implements Serializable {

	private static final long serialVersionUID = 5909591082086542952L;
	
	CLogger log = CLogger.getCLogger(Reception.class);

	ReceptionService receptionService = (ReceptionService) ServiceFactory.get(ReceptionService.class);
	InstallationService installationService = (InstallationService) ServiceFactory.get(InstallationService.class);

	List <KeyNamePair> warehouses = null;
	int selectedWarehouseId = 0;

	List <MBPartner> vendors = null;
	MBPartner selectedVendor;

	List <MOrder> orders = null;
	MOrder selectedOrder;

	List <ReceptionBean> lines = null;
	Map <Integer, Locator> locators = new HashMap<Integer, Locator>();
	Locator selectedLocator = null;
	int selectedLocatorId;
	String selectedLocatorName;
	
	List < ReceptionBean > selectedItems = new ArrayList<ReceptionBean>();
	boolean isInDispute = false;
	String textDispute = null;
	String message;
	
	List<MRefList> disputeReasons;
	
	int selectedRequestUserId = 0;
	

	@PostConstruct
	private void init() {
		  
		warehouses = receptionService.getWarehouses();
	}

	
	
	
	public List <MBPartner> getVendors() {

		if (selectedWarehouseId > 0) 
			vendors = receptionService.getVendors();
		else {
			vendors = null;
			orders = null;
		}

		return vendors;
	}

	
	public void setSelectedVendor(MBPartner selectedVendor) {

		this.selectedVendor = selectedVendor;

		if (selectedVendor != null && selectedWarehouseId != 0) 
			orders = receptionService.getPurchaseOrders(selectedVendor,selectedWarehouseId);
		else
			orders = null;
		
		lines = null;
		message = null;

	}


	public void setSelectedOrder(MOrder selectedOrder) {

		this.selectedOrder = selectedOrder;
		lines = receptionService.getOrderData(selectedOrder.getC_Order_ID(), false);
		String locatorName = locators.values().iterator().next().getName();
		for (ReceptionBean receptionBean : lines) {
			receptionBean.setLocator(locatorName);
		}
		message = null;
	}



	
	public void createReception(ActionEvent actionEvent) {
		
		if (selectedOrder.getPOReference() == null || selectedOrder.getPOReference().isEmpty()) {
			FacesUtil.addWarnMessage("Sales Order is not attached to Purchase Order");
			return;
		}
		
		
		selectedRequestUserId = installationService.getPreferenceAttributeAsInt(Envs.ROLE_WAREHOUSE);
				
		if (selectedRequestUserId <=0) {
			FacesUtil.addWarnMessage("Warehouse User is not defined. See preferences.");
			return;			
		}
		
		int accountingUserId = installationService.getPreferenceAttributeAsInt(Envs.ROLE_ACCOUNTING_RECEIVABLE);
		if (accountingUserId <=0) {
			FacesUtil.addWarnMessage("Accounting User is not defined. See preferences.");
			return;			
		}
		
		if (selectedItems == null || selectedItems.isEmpty()) {
			FacesUtil.addWarnMessage("Select lines");
			return;			
		}
		

		String trxName = Trx.createTrxName("createReception");
		Trx trx = Trx.get(trxName, true);

		selectedOrder.set_TrxName(trxName);


		log.log(Level.INFO, "Start Process...");

		MInOut ship = null;
		MInvoice invoice = null;
		String requestMsg = "";
		boolean hasLineToProceed = true;
		boolean hasAllLines = lines.size() == selectedItems.size();
		log.log(Level.INFO, "Total lines : "+lines.size()+" Selected Lines:"+selectedItems.size());
		
		if (isInDispute) {
			hasLineToProceed = false;	
			selectedOrder.set_CustomColumn("IsInDispute", "Y");
    		selectedOrder.save();	
    		
			log.log(Level.INFO, "Purchase Order is in dispute, #"+selectedOrder.getDocumentNo());
    		
			for (ReceptionBean line : selectedItems) {
				if (line.isInDispute()) {
					MOrderLine oline = new MOrderLine(Envs.getCtx(), line.getOrderLine().getKey(), trxName);
					oline.set_CustomColumn("Dispute_Reason", line.getTextDispute());
					boolean flag = oline.save();
				} else
					hasLineToProceed = true;
			}			

			FacesUtil.addSuccessMessage("PO Order #: %s is in Dispute", selectedOrder.getDocumentNo());			
			
		}
		
		if (hasLineToProceed) {
			ship = receptionService.createReception(selectedLocatorId, selectedOrder, selectedItems, isInDispute);
			ship.processIt(MInOut.ACTION_Complete);
			ship.setDocAction(DocAction.ACTION_Complete);
			ship.save(trxName);
			
			log.log(Level.INFO, "Reception is Created, #"+ship.getDocumentNo());

			if (ship !=null)
				invoice = receptionService.createInvoice(ship);
			
			log.log(Level.INFO, "Invoice is Created, #"+invoice.getDocumentNo());
			
			
			if (hasAllLines && !isInDispute) {
				selectedOrder.processIt(MOrder.ACTION_Close);
				requestMsg = "Close all";
	    		orders.remove(selectedOrder);
			} else {
				if (isInDispute) {
					requestMsg = "Partially reception and dispute line";
				} else {
					requestMsg = "Partially reception";

				}
				FacesUtil.addSuccessMessage(requestMsg+" #"+selectedItems.size());
//				selectedOrder.processIt(MOrder.ACTION_Complete);
			}
				
    		selectedOrder.save(trxName);

    		
			log.log(Level.INFO, "Purchase Order Closed - "+selectedOrder.getDocStatusName());
    		
			ship.processIt(MInOut.ACTION_Close);
			ship.save(trxName);
			
			log.log(Level.INFO, "Reception is Closed, #"+ship.getDocStatusName());

			int C_Order_ID = DB.getSQLValue(null, "SELECT C_Order_ID FROM C_Order WHERE C_Order_ID = ?", selectedOrder.getLink_Order_ID());
			MOrder so = new MOrder(Envs.getCtx(), C_Order_ID, null);
			log.log(Level.INFO, "Sales Order found, #"+so.getDocumentNo());
			

			if (trx.commit()) {		
				
				int R_RequestType_ID = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("R_RequestType_ID"));
				MRequest req = receptionService.createNewRequest(so, ship, invoice, R_RequestType_ID, accountingUserId, requestMsg);
			
				log.log(Level.INFO, "Request is created, #"+req.getDocumentNo());				
				
				if (C_Order_ID <= 0)
					FacesUtil.addWarnMessage("SO not found for PO: "+selectedOrder.getDocumentNo()+" add it manually to Request #"+req.getDocumentNo());				
				message = "Documents created: Reception #: %s, Invoice #: %s, Req # %s";
				FacesUtil.addSuccessMessage(message, ship.getDocumentNo(),invoice.getDocumentNo(),req.getDocumentNo());
			} else {
				trx.rollback();
				log.log(Level.WARNING, "Not proceed. Transaction Aborted, #"+selectedOrder.getDocumentNo());
				FacesUtil.addErrorMessage("Not proceed. Transaction Aborted");
			}

			trx.close();
			trx = null;
    		
			log.log(Level.INFO, "End Process");
    		
    		
		} 
		
		lines = null;
		selectedOrder = null;
		isInDispute = false;
		
	}
	
	
	
	

	public void cmdSearchReception(String filter) {
		selectedOrder = receptionService.getPurchaseOrder(filter);
		if (selectedOrder == null) {
			selectedVendor = null;
			orders = null;
			lines = null;
			message = "Purchase Order Not found";
			return;
		}

		message = null;

		selectedVendor = MBPartner.get(Envs.getCtx(), selectedOrder.getC_BPartner_ID());
		orders = receptionService.getPurchaseOrders(selectedVendor,selectedWarehouseId);
		lines = receptionService.getOrderData(selectedOrder.getC_Order_ID(), false);
	}

	public List < MOrder > getOrders() {
		return orders;
	}

	public List < KeyNamePair > getWarehouses() {
		return warehouses;
	}

	public MBPartner getSelectedVendor() {
		return selectedVendor;
	}

	public MOrder getSelectedOrder() {
		return selectedOrder;
	}

	public List < ReceptionBean > getLines() {
		return lines;
	}

	public List < ReceptionBean > getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List < ReceptionBean > selectedItems) {
		this.selectedItems = selectedItems;
	}

	public Collection <Locator> getFreeLocators() {

			return locators.values();
	}	
	


	public int getSelectedWarehouseId() {
		return selectedWarehouseId;
	}




	public void setSelectedWarehouseId(int selectedWarehouseId) {
		this.selectedWarehouseId = selectedWarehouseId;
		locators = receptionService.getFreeLocators(selectedWarehouseId);
		orders = null;
	}


	public boolean isInDispute() {
		return isInDispute;
	}

	public void setInDispute(boolean isInDispute) {
		this.isInDispute = isInDispute;
	}

	public String getTextDispute() {
		return textDispute;
	}

	public void setTextDispute(String textDispute) {
		this.textDispute = textDispute;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
/*	
	public void cmdPrintStockLabel(StockBean bean) throws IOException {
		receptionService.printLabel("SPOK_Print_etiquette", bean.getInoutId());

	}
*/

	public int getSelectedLocatorId() {
		return selectedLocatorId;
	}

	

	public void setSelectedLocatorId(int selectedLocatorId) {
		this.selectedLocatorId = selectedLocatorId;
		Locator knp = locators.get(selectedLocatorId);
		for (ReceptionBean receptionBean : lines) {
			receptionBean.setLocator(knp.getName());
		}
	}


	public void onLocatorChange() {
		
		System.out.println("SELECTED WAREHOUSE=== "+selectedWarehouseId);
	}



	public int getSelectedRequestUserId() {
		return selectedRequestUserId;
	}


	public void setSelectedRequestUserId(int selectedRequestUserId) {
		
		this.selectedRequestUserId = selectedRequestUserId;
	
	}



	public Locator getSelectedLocator() {
		return selectedLocator;
	}




	public void setSelectedLocator(Locator selectedLocator) {
		this.selectedLocator = selectedLocator;
	}





	
	
}