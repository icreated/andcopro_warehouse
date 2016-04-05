/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.compiere.model.MInOut;
import org.compiere.model.MRequest;
import org.compiere.model.MResource;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import com.andcopro.bean.InstallBean;
import com.andcopro.bean.OrderLine;
import com.andcopro.service.InstallationService;
import com.andcopro.service.ServiceFactory;
import com.andcopro.util.Envs;
import com.andcopro.util.FacesUtil;

/**
 *  Installation View.
 *  @author     Sergey Polyarus
 *  @version 1.0
 *  @since   2015-10-25
 */

@ManagedBean
@ViewScoped
public class Installation implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	CLogger log = CLogger.getCLogger(Installation.class);

	InstallationService installationService = (InstallationService)ServiceFactory.get(InstallationService.class);
  
  private static final String TAB_INSTALLS = "Installs";
  private static final String TAB_REDO = "Re-Do's";
  private static final String TAB_MEASURES= "Measures";
  private static final String TAB_THIRD_PARTY = "Third party";
  private static final String TAB_RECAP = "Recap";
  
  private List<InstallBean> lines;
  private List<OrderLine> orderLines;
  int selectedResourceTypeId = 0;
  int selectedResourceId = 0;
  int selectedId = 0;
  String selectedTab = null;

  private List<InstallBean> selectedLines;  
  
  @PostConstruct
  public void initTab() {
	  
	  selectedLines = new ArrayList<InstallBean>();
	  
	if (selectedTab == null)
		selectedTab = TAB_INSTALLS;
	  
//	System.out.println("------------- "+selectedTab);
	String M_Warehouse_ID = String.valueOf(Envs.getWebStore().getM_Warehouse_ID());
	
  	if (selectedTab.equals(TAB_INSTALLS)) {
		lines = installationService.getInstallationToGenerate(0, M_Warehouse_ID, "'Install'", "'Verify Payment','Assign Crew'");
  	} else if (selectedTab.equals(TAB_REDO)) {
		lines = installationService.getInstallationToGenerate(0, M_Warehouse_ID, "'Install','Purchase'", "'Redo'");
  	} else if (selectedTab.equals(TAB_MEASURES)) {
		lines = installationService.getMeasures();
//		lines = installationService.getInstallationToGenerate(0, M_Warehouse_ID, "'Purchase'", "'Schedule Measure'");
  	} else if (selectedTab.equals(TAB_THIRD_PARTY)) {
		MWarehouse[] warehouses = MWarehouse.getForOrg(Envs.getCtx(), Env.getAD_Org_ID(Envs.getCtx()));
		M_Warehouse_ID = "";
		for (MWarehouse warehouse : warehouses) {
			if (warehouse.getM_Warehouse_ID() != Envs.getWebStore().getM_Warehouse_ID())
				M_Warehouse_ID += warehouse.getM_Warehouse_ID()+",";
		}
		if (M_Warehouse_ID.length() > 0)
			M_Warehouse_ID = M_Warehouse_ID.substring(0, M_Warehouse_ID.length()-1);
		lines = installationService.getInstallationToGenerate(0, M_Warehouse_ID, "'Install'", "'Verify Payment','Assign Crew'"); 
	} else if (selectedTab.equals(TAB_RECAP)) {
		lines = installationService.getRecapInstallation();
	}


  }
  
  
  
  public void generate() {
	  
	  log.log(Level.INFO, "Generate : "+selectedTab+"  selected lines = "+selectedLines.size());
	  
	  MResource resource = MResource.get(Envs.getCtx(), selectedResourceId);
	  String message = null;
	  
	  
	  if (selectedLines.size() == 0) {
		  FacesUtil.addWarnMessage("Lines not selected");
		  return;
	  }
	  
	  
	  if (selectedTab.equals(TAB_INSTALLS)) {
		  
		  message = installationService.generateShipments(selectedLines, DocAction.ACTION_None, Envs.getWebStore().getM_Warehouse_ID());
		  if (message.startsWith("ERROR")) {
			  FacesUtil.addErrorMessage(message);
			  return;
		  } else if (!message.equals("0")) {
			  
			  message = "Installation : "+message;
			  int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_WAREHOUSE, null);
			  for(InstallBean order: selectedLines) {
				  installationService.updateStatusRequest(order.getRequestId(), AD_User_ID, InstallationService.STATUS_INSTALL_SCHEDULED, 
						  "Installation Scheduled - "+resource.getName(), null);
			  }					  
		  }
  
	  

		  
	  } else if (selectedTab.equals(TAB_REDO)) {
		  
		  int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_WAREHOUSE, null);		  
		  for (InstallBean install : selectedLines) {
			  installationService.updateStatusRequest(install.getRequestId(), AD_User_ID, InstallationService.STATUS_INSTALLATION, 
					  "Installation - "+resource.getName(), null);
		  }

	  } else if (selectedTab.equals(TAB_MEASURES)) {
		  
		  int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_PURCHASING, null);
		  for (InstallBean install : selectedLines) {
			  installationService.updateStatusRequest(install.getRequestId(), AD_User_ID, InstallationService.STATUS_SCHEDULE_PO, 
					  "Schedule PO - "+resource.getName(), null);
		  } 
	  } else if (selectedTab.equals(TAB_THIRD_PARTY)) {
			  
		  
		  message = installationService.generateShipments(selectedLines, DocAction.ACTION_None, Envs.getWebStore().getM_Warehouse_ID());
		  if (message.startsWith("ERROR")) {
			  FacesUtil.addErrorMessage(message);
			  return;
		  } else if (!message.equals("0")) {
			  
			  message = "Installation : "+message;		  
			  for(InstallBean order: selectedLines) {
				  installationService.updateStatusRequest(order.getRequestId(), 0, InstallationService.STATUS_INSTALL_SCHEDULED, 
						  "Installation Scheduled - "+resource.getName(), null);
			  }				  
		  }
		  
			  

	  } else if (selectedTab.equals(TAB_RECAP)) {
		  
		  Map<Integer, String> map = new HashMap<Integer, String>();
		  for(InstallBean install : selectedLines) {
			  map.put(install.getShipmentId(), install.getDocStatus());
		  }
		  
		  
		  for( Map.Entry<Integer, String>install : map.entrySet()) {
			  
			  String trxName = Trx.createTrxName("Recap");	
			  Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
			  
			  MInOut ship = new MInOut(Envs.getCtx(), install.getKey(), trxName);	
			  
			  if (install.getValue().equals(DocAction.STATUS_Drafted) || install.getValue().equals(DocAction.STATUS_InProgress)) {
				  
				  ship.setDocAction(DocAction.ACTION_Complete);
				  ship.processIt(DocAction.ACTION_Complete);
				  ship.saveEx();
				  
				  int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_SCHEDULING, trxName);
				  installationService.updateRequests(install.getKey(), AD_User_ID, InstallationService.STATUS_INSTALLATION, 
							"Installation - "+resource.getName(), trxName);					  

			  } else if (install.getValue().equals(DocAction.STATUS_Completed)) {
				  
				  ship.processIt(DocAction.ACTION_Close);
				  ship.saveEx();
				  
				  int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_ACCOUNTING_RECEIVABLE, trxName);
				  installationService.updateRequests(install.getKey(), AD_User_ID, InstallationService.STATUS_INSTALL_COMPLETE, 
							"Install Complete - "+resource.getName(), trxName);					  
			  }
			  

			   if (!trx.commit()) {
				  	trx.rollback();
					FacesUtil.addErrorMessage("Transaction is aborted #"+ship.getDocumentNo());	
				}
				trx.close();
				trx = null;		

		  }
		  
	  
			  

	  }
	  
	  
	  initTab();
	  if (message != null && message.length() > 0)
		  FacesUtil.addSuccessMessage(message);
  }

 
  public void addToSelection(InstallBean installBean) {
	  
	  if (installBean.isSelected())
		  selectedLines.add(installBean);
	  else
		  selectedLines.remove(installBean);
  }
  
  
  
  
  public void redoInstallation(InstallBean install) {
	  
	  MResource resource = MResource.get(Envs.getCtx(), selectedResourceId);
	  
	  String trxName = Trx.createTrxName("REDO");	
	  Trx trx = Trx.get(trxName, true);

	  installationService.updateRequests(install.getShipmentId(), 0, InstallationService.STATUS_REDO, 
			  "Redo - "+resource.getName(), trxName);	  
	  
		if (trx.commit()) {
			FacesUtil.addSuccessMessage("Installation %s has been redone", install.getShipDocumentNo());	
		} else {
			trx.rollback();
			FacesUtil.addErrorMessage("Cannot redo installation # %s", install.getShipDocumentNo());	
		}
		trx.close();
		trx = null; 
	  
	  lines = installationService.getRecapInstallation();
  }
  
  
  
  public void voidInstallation(InstallBean install) {
	  

		MResource resource = MResource.get(Envs.getCtx(), selectedResourceId);
	  
		String trxName = Trx.createTrxName("DEL_SHIP");	
		Trx trx = Trx.get(trxName, true);
	  
		int AD_User_ID = installationService.getPreferenceAttributeAsInt(Envs.ROLE_SCHEDULING, trxName);
		  
		installationService.voidInstallation(install.getShipmentId(),trxName);	
		
		installationService.updateRequests(install.getShipmentId(), AD_User_ID, InstallationService.STATUS_ASSIGN_CREW, 
				InstallationService.STATUS_ASSIGN_CREW+ " - "+resource.getName(), trxName);	
		
		DB.executeUpdate("DELETE FROM S_ResourceAssignment WHERE S_ResourceAssignment_ID = ?", install.getEventId(), trxName);

		if (trx.commit()) {
			FacesUtil.addSuccessMessage("Installation %s is voided", install.getShipDocumentNo());	
		} else {
			trx.rollback();
			FacesUtil.addErrorMessage("Cannot void installation # %s", install.getShipDocumentNo());	
		}
		trx.close();
		trx = null;
		
		lines = installationService.getRecapInstallation();

	  
  }
  

  public List<OrderLine> getOrderLines() {
	  return orderLines;
  }
  

	public int getSelectedResourceTypeId() {
		return selectedResourceTypeId;
	}
	
	
	public void setSelectedResourceTypeId(int selectedResourceTypeId) {
		this.selectedResourceTypeId = selectedResourceTypeId;
		
	}
	
	
	public int getSelectedResourceId() {
		return selectedResourceId;
	}
	
	
	public void setSelectedResourceId(int selectedResourceId) {
		this.selectedResourceId = selectedResourceId;
		
//		updateModel();
	}


	public void reloadLines() {
		
//		  lines = installationService.getInstallationToGenerate(0, 0, statusName, requestTypeName);	 
		String OrderId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myOrderId");
		int orderId = Integer.parseInt(OrderId);

		for (InstallBean install : lines) {
			if (install.getOrderId() == orderId) {
				install.setScheduleList(installationService.getResourceAssignements(orderId));
			}
		}
	}
	

	public void reloadCalendar() {
		  RequestContext requestContext = RequestContext.getCurrentInstance();
		  requestContext.execute(String.format("reloadCalendar(%s)", selectedResourceId));
	}




    public void initOrderLines(int M_InOut_ID) {
		  orderLines = installationService.getOrderLines2(M_InOut_ID);
    }

    public void onTabChange(TabChangeEvent event) {
    	selectedTab = event.getTab().getTitle();
    	initTab();
 //	MPreference

    }

    
    public List<InstallBean> getLines() { 
  	  return lines;
    }
    

   public int getSelectedId() {
		return selectedId;
	}



	public void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
	}





    
}
