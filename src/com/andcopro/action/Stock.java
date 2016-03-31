/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 13 nov. 2015
 */
package com.andcopro.action;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.compiere.model.MCharge;
import org.compiere.model.MInventory;
import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MStorageOnHand;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.andcopro.bean.Locator;
import com.andcopro.bean.MovementBean;
import com.andcopro.service.ReceptionService;
import com.andcopro.service.ServiceFactory;
import com.andcopro.service.StockService;
import com.andcopro.util.Envs;
import com.andcopro.util.FacesUtil;

@ManagedBean
@ViewScoped
public class Stock implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	CLogger log = CLogger.getCLogger(Stock.class);

	
	private static final String TAB_MOVEMENT = "tabMovement";
	private static final String TAB_INVENTORY = "tabInventory";
	  
	StockService stockService = (StockService) ServiceFactory.get(StockService.class);
	ReceptionService receptionService = (ReceptionService) ServiceFactory.get(ReceptionService.class);	

	Map <Integer, Locator> locators = new HashMap<Integer, Locator>();	
	Map <Integer, Locator> locatorsTo = new HashMap<Integer, Locator>();
	KeyNamePair product;
	Locator locator;
	Locator locatorTo;
	BigDecimal	qtyOnHand = null;
	BigDecimal	qty;
	List<MovementBean> lines;
	String selectedTab = null;
	boolean isInStock = false;
	MCharge charge;
	
	
	  @PostConstruct
	  public void initTab() {

		if (selectedTab == null)
			selectedTab = TAB_MOVEMENT;
		
		lines = new ArrayList<MovementBean>();
		locatorsTo = receptionService.getFreeLocators(Envs.getWebStore().getM_Warehouse_ID());
		locators = new HashMap<Integer, Locator>(locatorsTo);
        product = null;
        locator = null;
        locatorTo = null;
        qtyOnHand = null;
        qty = null;
        charge = null;
	  }
	  


	  public void onProductItemSelect(SelectEvent event) {

		KeyNamePair knp = (KeyNamePair)event.getObject();
		
		qtyOnHand = MStorageOnHand.getQtyOnHand(knp.getKey(), Envs.getWebStore().getM_Warehouse_ID(), 0, null);
		isInStock = qtyOnHand.compareTo(BigDecimal.ZERO) > 0;
		
		if (isInStock) {
			MStorageOnHand[] storages = MStorageOnHand.getOfProduct(Envs.getCtx(), knp.getKey(), null);
			locators = new HashMap<Integer, Locator>();

			for (MStorageOnHand storage : storages) {
				MLocator loc = MLocator.get(Envs.getCtx(), storage.getM_Locator_ID());
				locators.put(loc.getM_Locator_ID(), new Locator(loc.getM_Locator_ID(), loc.isDefault(),loc.getValue(), true, loc.getPriorityNo()));
			}
			
			if (locators.size() > 0 && storages.length > 0 ) {
				locator = locators.values().iterator().next();
				qtyOnHand = MStorageOnHand.getQtyOnHandForLocator(knp.getKey(), locator.getId(), 0, null);
			}
			
		} else
			locators = locatorsTo;

	  }
	  
	  public void onLocatorItemSelect() {
		  
		  if (locator != null && product != null)
			  qtyOnHand = MStorageOnHand.getQtyOnHandForLocator(product.getKey(), locator.getId(), 0, null);
	  }
	
	
	  public void generate() {
		  
		  
		  if (lines == null || lines.size() == 0) {
			  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Warning","Lines not created"));
			  return;
		  }
		  
		  boolean ok = false;
		  
		  if (selectedTab.equals(TAB_MOVEMENT)) {
			  
			  MMovement mov = stockService.createMovement(lines);
			  if (mov != null) {
				  FacesUtil.addSuccessMessage("Movement created: # %s", mov.getDocumentNo());
				  ok = true;
			  }
			  
		  } else if (selectedTab.equals(TAB_INVENTORY)) {
			  
			  MInventory inventory = stockService.createInventory(lines);
			  if (inventory != null) {
				  FacesUtil.addSuccessMessage("Inventory created: # %s", inventory.getDocumentNo());
				  ok = true;
			  }
		  }
		  
		  if (!ok) {
				log.log(Level.WARNING, "Not proceed. Transaction Aborted");
				FacesUtil.addErrorMessage("Not proceed. Transaction Aborted");
				return;
		  }

		  initTab();
	  }
	  


	public List<KeyNamePair> completeProduct(String filter) {

    	return stockService.getProducts(filter);
    }
    
	
    
	public Collection <Locator> getLocators() {

		return locators.values();
	}	

	
	
	
    public void add() {
    	

        MovementBean mov = null;
        if (selectedTab.equals(TAB_MOVEMENT))
        	mov = new MovementBean(product, locator, locatorTo, qtyOnHand);
        else if (selectedTab.equals(TAB_INVENTORY))
        	mov = new MovementBean(product, locator, qtyOnHand, qty, charge);
        
        lines.add(mov);
 
        product = null;
        locator = null;
        locatorTo = null;
        qtyOnHand = null;
        qty = null;
        charge = null;
        
    }
    
    

	public KeyNamePair getProduct() {
		return product;
	}

	public void setProduct(KeyNamePair product) {
		if (product != null)
			this.product = product;
	}

	public  Locator getLocator() {
		return locator;
	}

	public void setLocator(Locator locator) {

		if (locator != null) {
			this.locator = locator;
		}
	}

	public Locator getLocatorTo() {
		return locatorTo;
	}
	

	public Map<Integer, Locator> getLocatorsTo() {
		return locatorsTo;
	}

	public void setLocatorTo(Locator locatorTo) {
		
		if (locatorTo != null)
			this.locatorTo = locatorTo;
	}

	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}

	public void setQtyOnHand(BigDecimal qtyOnHand) {
		if (qtyOnHand != null)
			this.qtyOnHand = qtyOnHand;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public List<MovementBean> getLines() {
		return lines;
	}

	
    public MCharge getCharge() {
		return charge;
	}



	public void setCharge(MCharge charge) {

		this.charge = charge;
	}



	public void onTabChange(TabChangeEvent event) {
    	selectedTab = event.getTab().getId();
    	initTab();
    }

    
    
    
    
}
