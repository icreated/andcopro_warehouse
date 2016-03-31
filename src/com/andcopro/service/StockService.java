/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MStore;
import org.compiere.model.MWarehouse;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Trx;

import com.andcopro.bean.Locator;
import com.andcopro.bean.MovementBean;
import com.andcopro.bean.StockBean;
import com.andcopro.bean.WUser;
import com.andcopro.util.Envs;

public class StockService  extends AService {

	

	CLogger log = CLogger.getCLogger(StockService.class);
	ReceptionService receptionService;	

	StockService(Properties ctx, WUser sessionUser, MStore webStore) {
		super(ctx, sessionUser, webStore);
		receptionService = ServiceFactory.get(ReceptionService.class);

	}	

	
	public List<KeyNamePair> getProducts(String filter) {
		
			
		String sql = "SELECT M_Product_ID, Name FROM M_Product "+
				"WHERE isActive='Y' AND AD_Client_ID = ? AND UPPER(Name) LIKE UPPER(?)" ;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<KeyNamePair> list = new ArrayList<KeyNamePair>();
	
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, Env.getAD_Client_ID(Envs.getCtx()));
			pstmt.setString(2, filter+"%");
			rs = pstmt.executeQuery();
	
			while (rs.next()) {
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
				
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		return list;
	
	}
	
	
	public List<KeyNamePair> getLocators(String filter) {
		
		
		String sql = "SELECT M_Locator_ID, Value FROM M_Locator "+
				"WHERE isActive='Y' AND AD_Client_ID = ? AND UPPER(Value) LIKE UPPER(?)" ;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<KeyNamePair> list = new ArrayList<KeyNamePair>();
	
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, Env.getAD_Client_ID(Envs.getCtx()));
			pstmt.setString(2, filter+"%");
			rs = pstmt.executeQuery();
	
			while (rs.next()) {
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
				
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return list;
	
	}
	
	
	public MMovement createMovement(List<MovementBean> lines) {
		
		String trxName = Trx.createTrxName("Movement");	
		Trx trx = Trx.get(trxName, true);
		
		  MMovement movement = new MMovement(ctx, 0, trxName);
		  movement.setAD_Org_ID(Env.getAD_Org_ID(ctx));
		  movement.setMovementDate(new Timestamp(System.currentTimeMillis()));
		  movement.save();
		  
		  for (MovementBean bean : lines) {
				MMovementLine line = new MMovementLine(movement);
				line.setM_Product_ID(bean.getProduct().getKey());
				line.setM_Locator_ID(bean.getLocator().getId());
				line.setM_LocatorTo_ID(bean.getLocatorTo().getId());
				line.setMovementQty(bean.getQtyOnHand());
				line.saveEx();	
			}
	
		
		 movement.processIt(MMovement.ACTION_Complete);
		 movement.save();
		  
			if (trx.commit()) {
				return movement;
			} else {
				trx.rollback();
				return null;	
			}
		
	}
	
	
	
	public MInventory createInventory(List<MovementBean> lines) {
		

		
		MDocType docType = null;
		MDocType[] types = MDocType.getOfDocBaseType(ctx, MDocType.DOCBASETYPE_MaterialPhysicalInventory);
		for (MDocType mDocType : types) {
			if (mDocType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_InternalUseInventory)) {
				docType = mDocType;
				break;
			}
		}
		if (docType == null)
			return null;
		
		
		String trxName = Trx.createTrxName("Inventory");	
		Trx trx = Trx.get(trxName, true);
		String msg;
		
		MInventory inventory = new MInventory(ctx, 0, trxName);
		inventory.setAD_Org_ID(Env.getAD_Org_ID(ctx));
		inventory.setC_DocType_ID(docType.getC_DocType_ID());
		inventory.setMovementDate(new Timestamp(System.currentTimeMillis()));
		inventory.save();

		  
		  for (MovementBean bean : lines) {
			  MInventoryLine line = new MInventoryLine(inventory, bean.getLocator().getId(), 
					  bean.getProduct().getKey(),0,null,null,bean.getQty());
			  	line.setInventoryType(MInventoryLine.INVENTORYTYPE_ChargeAccount);
				line.setC_Charge_ID(bean.getCharge().getC_Charge_ID());
				line.saveEx();	
			}
	
		
		 inventory.processIt(MMovement.ACTION_Complete);
		 inventory.save();
		  
			if (trx.commit()) {
				return inventory;
			} else {
				trx.rollback();
				return null;	
			}
		
	}
	
	
	
	public MLocator getDefaultLocator() {
		
		MWarehouse  warehouse = MWarehouse.get(ctx, webStore.getM_Warehouse_ID());
		MLocator locator = MLocator.getDefault(warehouse);
		return locator;
	}
	
	
	public List<StockBean> getStockLocators() throws AdempiereException {
		
		
		
		String sql = "SELECT DISTINCT l.x || l.y || l.z locator, o.DocumentNo PO_DocNo, o.POReference SO_DocNo, " +
				"quote.DocumentNo Quote_DocNo, so.POReference costcoNo, " +
				"o.C_Order_ID, l.M_Locator_ID, bp.Name, iol.M_InOut_ID, " +
				"l.IsDefault, l.Value, l.PriorityNo FROM M_Locator l " +
				"LEFT JOIN M_InOutLine iol ON l.M_Locator_ID = iol.M_Locator_ID " +
				"INNER JOIN C_OrderLine ol ON ol.C_OrderLine_ID = iol.C_OrderLine_ID " +
				"INNER JOIN C_Order o ON ol.C_Order_ID = o.C_Order_ID " +
				"LEFT JOIN C_Order quote ON o.Link_Order_ID = quote.C_Order_ID " +
				"LEFT JOIN C_BPartner bp ON bp.C_BPartner_ID = quote.C_BPartner_ID " +
				"LEFT JOIN C_Order so ON so.DocumentNo = o.POReference " +
				"WHERE l.isActive='Y' AND o.isSOTrx = 'N' AND l.M_Locator_ID != ? " +
				"ORDER BY locator";
		List<StockBean> list = new ArrayList<StockBean>();
		MLocator dlocator = getDefaultLocator();
		if (dlocator == null)
			throw new AdempiereException ("The default locator is not defined");
		int M_Default_Locator_ID = getDefaultLocator().getM_Locator_ID();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt(1, M_Default_Locator_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ()) {
				StockBean bean = new StockBean(rs.getString(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5), 
						rs.getInt(6), rs.getInt(7), rs.getString(8));
				bean.setSelectedLocator(new Locator(rs.getInt(7), rs.getString(10).equals("Y"), rs.getString(11), true, rs.getInt(12)));
				bean.setInoutId(rs.getInt(9));
				list.add(bean);
			}
		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, sql, ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return list;
	}	
	
	
	public void setLocatorToReceptions(int C_Order_ID, int M_Locator_ID) {
		
		
		if (M_Locator_ID == 0)
			M_Locator_ID = getDefaultLocator().getM_Locator_ID();
		
		
		String sql = "SELECT iol.M_InOutLine_ID FROM M_InOut io " +
				"INNER JOIN M_InOutLine iol ON io.M_InOut_ID = iol.M_InOut_ID " +
				"WHERE io.C_Order_ID = ? ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt(1, C_Order_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ()) {
				MInOutLine line = new MInOutLine(ctx, rs.getInt(1),null);
				line.setM_Locator_ID(M_Locator_ID);
				line.save();
			}

		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, sql, ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

	}
	

	
	
	public void prepareTemporaryTable(StockBean bean) {
		
		
		DB.executeUpdate("DELETE FROM T_INOUT_LABEL WHERE AD_User_ID = "+sessionUser.getAD_User_ID(), null);
		
		
		String sql = "SELECT M_InOutLine_ID, qtyBox FROM M_InOutLine WHERE M_InOut_ID = ?";
		
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt(1, bean.getInoutId());
			rs = pstmt.executeQuery ();
			while (rs.next ()) {
				
				int count = rs.getInt(2);
				for(int i=1; i<=count;i++) {
					
					String sql2 = "INSERT INTO T_INOUT_LABEL(AD_User_ID, M_InOutLine_ID, Qty) " +
					"VALUES ("+sessionUser.getAD_User_ID()+", "+rs.getInt(1)+", "+i+")";
					
					DB.executeUpdate(sql2, null);
				}
				
			}

		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, sql, ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}		
		
		
	}
	

	  

	  
	  
}
