/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 6 nov. 2015
 */
package com.andcopro.action;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.compiere.model.MUser;
import org.compiere.util.CLogger;

import com.andcopro.service.InstallationService;
import com.andcopro.service.ServiceFactory;
import com.andcopro.util.Envs;

@ManagedBean
@ViewScoped
public class Preference implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	CLogger log = CLogger.getCLogger(Preference.class);
	InstallationService installationService = (InstallationService)ServiceFactory.get(InstallationService.class);
	

	MUser warehouse;
	MUser scheduler;
	MUser accounting;
	MUser purchasing;
	
	List<MUser> warehouseList;
	List<MUser> schedulerList;
	List<MUser> accountingList;
	List<MUser> purchasingList;
	
	
	@PostConstruct
	public void init() {
		
		String userID = installationService.getPreferenceAttribute(Envs.ROLE_WAREHOUSE, null);
		if (userID != null)
			warehouse = MUser.get(Envs.getCtx(), Integer.parseInt(userID));
		
		userID = installationService.getPreferenceAttribute(Envs.ROLE_SCHEDULING, null);
		if (userID != null)
			scheduler = MUser.get(Envs.getCtx(), Integer.parseInt(userID));
		
		userID = installationService.getPreferenceAttribute(Envs.ROLE_ACCOUNTING_RECEIVABLE, null);
		if (userID != null)
			accounting = MUser.get(Envs.getCtx(), Integer.parseInt(userID));
		
		userID = installationService.getPreferenceAttribute(Envs.ROLE_PURCHASING, null);
		if (userID != null)
			purchasing = MUser.get(Envs.getCtx(), Integer.parseInt(userID));
		
		warehouseList = installationService.getUsersByRoleName(Envs.ROLE_WAREHOUSE, null);

		schedulerList = installationService.getUsersByRoleName(Envs.ROLE_SCHEDULING, null);
		accountingList = installationService.getUsersByRoleName(Envs.ROLE_ACCOUNTING_RECEIVABLE, null);
		purchasingList = installationService.getUsersByRoleName(Envs.ROLE_PURCHASING, null);

		
	}
	
	public void save() {

		StringBuffer msg = new StringBuffer();
		
		if (warehouse != null && warehouse.getAD_User_ID() > 0) {
			installationService.setPreferenceAttribute(Envs.ROLE_WAREHOUSE,String.valueOf(warehouse.getAD_User_ID()), null);
			msg.append("Warehouse:").append(warehouse.getName()).append(", ");
		}	
		if (purchasing != null && purchasing.getAD_User_ID() > 0) {
			installationService.setPreferenceAttribute(Envs.ROLE_PURCHASING,String.valueOf(purchasing.getAD_User_ID()), null);
			msg.append("Purchasing:").append(purchasing.getName()).append(", ");
		}
		if (accounting != null && accounting.getAD_User_ID() > 0) {
			installationService.setPreferenceAttribute(Envs.ROLE_ACCOUNTING_RECEIVABLE,String.valueOf(accounting.getAD_User_ID()), null);
			msg.append("Accounting:").append(accounting.getName()).append(", ");
		}
		if (scheduler != null && scheduler.getAD_User_ID() > 0) {
			installationService.setPreferenceAttribute(Envs.ROLE_SCHEDULING,String.valueOf(scheduler.getAD_User_ID()), null);
			msg.append("Scheduler:").append(scheduler.getName()).append(", ");
		}
		
		log.info("Properties saved");
		
		
		if (msg.length() > 0) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO,"Success","Updated - " +msg.substring(0, msg.length()-2)));
		}
		
	}
	
	

	
	
    public List<MUser> completeWarehouse(String filter) {

    	return installationService.getUsersByRoleName(Envs.ROLE_WAREHOUSE, filter);
    }
    
    public List<MUser> completeScheduler(String filter) {

    	return installationService.getUsersByRoleName(Envs.ROLE_SCHEDULING, filter);
    }    
    
    public List<MUser> completeAccounting(String filter) {

    	return installationService.getUsersByRoleName(Envs.ROLE_ACCOUNTING_RECEIVABLE, filter);
    }  
 
    public List<MUser> completePurchasing(String filter) {

    	return installationService.getUsersByRoleName(Envs.ROLE_PURCHASING, filter);
    }  
    

	public MUser getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(MUser user) {
		this.warehouse = user;
	}

	public MUser getScheduler() {
		return scheduler;
	}

	public void setScheduler(MUser scheduler) {
		this.scheduler = scheduler;
	}

	public MUser getAccounting() {
		return accounting;
	}

	public void setAccounting(MUser accounting) {
		this.accounting = accounting;

	}

	public MUser getPurchasing() {
		return purchasing;
	}

	public void setPurchasing(MUser purchasing) {
		this.purchasing = purchasing;

	}

	public InstallationService getInstallationService() {
		return installationService;
	}

	public List<MUser> getWarehouseList() {
		return warehouseList;
	}

	public List<MUser> getSchedulerList() {
		return schedulerList;
	}

	public List<MUser> getAccountingList() {
		return accountingList;
	}

	public List<MUser> getPurchasingList() {
		return purchasingList;
	}

    
    

}
