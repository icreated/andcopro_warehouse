/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MStore;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.andcopro.bean.WUser;

public class OrderService  extends AService {

	

	CLogger log = CLogger.getCLogger(OrderService.class);
	
	
	OrderService(Properties ctx, WUser sessionUser, MStore webStore) {
		super(ctx, sessionUser, webStore);

	}	


	
	static String msg;

	
	public MDocType getDocSubTypeSO(String docSubType) {
		MDocType[] docTypes = MDocType.getOfDocBaseType(ctx, MDocType.DOCBASETYPE_SalesOrder);
		for (int i = 0; i < docTypes.length; i++) {
			if (docTypes[i].getDocSubTypeSO().equals(docSubType))
				return docTypes[i];
		}
		
		return docTypes[0];
	}
	
	
	
	public List<MOrder> getSearchingOrders(String searching, String type) {
		
		if (type == null)
			return null;

		List<MOrder> list = new ArrayList<MOrder>();
		
		String sql = "SELECT * FROM (SELECT * FROM C_Order WHERE DocStatus NOT IN ('DR') ";
		
		if (type.equals("BP"))
			sql += "AND C_BPartner_ID IN (SELECT C_BPartner_ID FROM C_BPartner WHERE UPPER(Name) LIKE UPPER(?)) ";
		else if(type.equals("ALL"))
			sql += "AND UPPER(DocumentNo)  LIKE ? OR UPPER(POReference)  LIKE UPPER(?) ";		
		else if(type.equals("OV"))
			sql += "AND UPPER(DocumentNo)  LIKE ? OR UPPER(POReference)  LIKE UPPER(?) " +
					"AND Bill_BPartner_ID=? ";
		else if(type.equals("SR"))
			sql += "AND UPPER(DocumentNo)  LIKE ? OR UPPER(POReference)  LIKE UPPER(?) " +
					"OR UPPER(Description) LIKE ? " +
					"AND SalesRep_ID=? ";
				
		sql +="ORDER BY DocumentNo DESC)  WHERE rownum <= 30";


		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			
			pstmt.setString(1, searching);
			if(type.equals("OV")) {
				pstmt.setString(2, searching);
				pstmt.setInt(3, sessionUser.getC_BPartner_ID());
			} else if(type.equals("ALL")) {
				pstmt.setString(2, searching);
			} else if(type.equals("SR")) {
				pstmt.setString(2, searching);
				pstmt.setString(3, searching);
				pstmt.setInt(4, sessionUser.getAD_User_ID());
			} 
			
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MOrder (ctx, rs, null));
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
		log.fine("#" + list.size());

		return list;
	}	//	getOrders		
	
	
	

	public List<MInvoice> getSearchingInvoices(String searching, String type) {
		
		if (type == null)
			return null;

		List<MInvoice> list = new ArrayList<MInvoice>();
		
		String sql = "SELECT * FROM (SELECT * FROM C_Invoice WHERE DocStatus NOT IN ('DR') ";
		
		if (type.equals("BP"))
			sql += "AND C_BPartner_ID IN (SELECT C_BPartner_ID FROM C_BPartner WHERE UPPER(Name) LIKE UPPER(?)) ";
		else if(type.equals("OV"))
			sql += "AND UPPER(DocumentNo)  LIKE ? " +
					"AND C_BPartner_ID=? ";
		else if(type.equals("SR"))
			sql += "AND (UPPER(DocumentNo)  LIKE ? OR UPPER(Description)  LIKE ?) " +
					"AND SalesRep_ID=? ";
				
		sql +="ORDER BY DocumentNo DESC)  WHERE rownum <= 30";


		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			
			pstmt.setString(1, searching);
			if(type.equals("OV")) {
				pstmt.setInt(2, sessionUser.getC_BPartner_ID());
			} else if(type.equals("SR")) {
				pstmt.setString(2, searching);
				pstmt.setInt(3, sessionUser.getAD_User_ID());
			} 
			
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInvoice (ctx, rs, null));
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
		log.fine("#" + list.size());
		return list;
	}	
	
	

	
	public List<MOrder> getOrders(boolean bySalesRep) {

		
		MDocType dt = getDocSubTypeSO(MDocType.DOCSUBTYPESO_Proposal);

		List<MOrder> list = new ArrayList<MOrder>();

		String sql = "SELECT * FROM C_Order WHERE ";
		
		if (bySalesRep)
			sql +="SalesRep_ID=? ";
		else
			sql +="Bill_BPartner_ID=? ";
					
		sql+= "AND DocStatus NOT IN ('DR') AND C_DocType_ID != ? ORDER BY DocumentNo DESC";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			if (bySalesRep)
				pstmt.setInt(1, sessionUser.getAD_User_ID());
			else
				pstmt.setInt(1, sessionUser.getC_BPartner_ID());
			
			pstmt.setInt(2, dt.getC_DocType_ID());
			
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MOrder (ctx, rs, null));
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
		log.fine("#" + list.size());
		return list;
	}	//	getOrders	
	
	

	public List<MOrder> getOrders(int C_BPartner_ID) {

		List<MOrder> list = new ArrayList<MOrder>();
		
		if (C_BPartner_ID == 0)
			C_BPartner_ID = sessionUser.getC_BPartner_ID();
		
		String sql = "SELECT * FROM C_Order WHERE C_BPartner_ID=? " +
				"AND DocStatus NOT IN ('DR') AND C_DocTypeTarget_ID IN (SELECT C_DocType_ID FROM C_DocType WHERE  " +
				"DocBaseType = 'SOO')  " +
				"ORDER BY DocumentNo DESC " ; //  AND DocSubTypeSO IN ('WI' )
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql, null);
//			pstmt.setInt(1, m_sessionUser.getAD_User_ID());
			pstmt.setInt(1, C_BPartner_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MOrder (ctx, rs, null));
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
		log.fine("#" + list.size());

		return list;
	}	//	getOrders	
	
	

	

	public void createOrderLine(MOrder order, MProduct product, BigDecimal price) {

		MOrderLine ol = getOrderLine(order, product, true);

		if (ol == null)
			ol = new MOrderLine(order);
		
		ol.setProduct(product);
		ol.setDescription(product.getDescription());
		ol.setPrice();
		ol.setPrice(price);
		ol.setTax();
		ol.setQty(Env.ONE);
		ol.save();	
		
		
	}
	

	
	
	

	public boolean processOrder (String DocAction, MOrder order)
	{
		if (DocAction == null || DocAction.length() == 0)
			return false;

		order.setDocAction (DocAction, true);	//	force creation
		boolean ok = order.processIt (DocAction);
		order.save();
		return ok;
	}	//	processOrder	
	
	
	
	
	
	

	
	public static MOrderLine getOrderLine(MOrder order, MProduct product, boolean reload) {
		
		MOrderLine[] lines = order.getLines(reload, null);
		MOrderLine ol = null;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].getM_Product_ID() == product.getM_Product_ID()) {
				ol = lines[i];
				break;
			}
		} 
		return ol;
		
	}
	
	

	
	
	public MOrder getOrder(int C_Order_ID) {
	
		return new MOrder(ctx, C_Order_ID,null);
	}

	public MInOut getShipment(int M_InOut_ID) {
		
		return new MInOut(ctx, M_InOut_ID,null);
	}
	
	public MInvoice getInvoice(int C_Invoice_ID) {
		
		return new MInvoice(ctx, C_Invoice_ID,null);
	}
	
	
	
	
	
	/**
	 * 	Get Shipments
	 *	@return shipments of BP
	 */
	public List<MInOut> getShipments()
	{
		
		List<MInOut> list = new ArrayList<MInOut>();
		
		String sql = "SELECT * FROM M_InOut WHERE C_BPartner_ID=?"
			+ " AND DocStatus NOT IN ('DR') "
			+ " ORDER BY DocumentNo DESC";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, sessionUser.getC_BPartner_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInOut (ctx, rs, null));
			
			DB.close(rs, pstmt);
			pstmt = null;
		}
		catch (Exception e)
		{
				log.log(Level.SEVERE, "getShipments", e);
		}

		return list;
	}	//	getShipments
	
	

	public List<MInvoice> getInvoices(boolean bySalesRep) {

	
		List<MInvoice> list = new ArrayList<MInvoice>();
		
		
		String sql = "SELECT * FROM C_Invoice WHERE ";
		
		if (bySalesRep)
			sql +="SalesRep_ID=? ";
		else
			sql +="C_BPartner_ID=? ";
					
		sql+= "AND DocStatus NOT IN ('DR') ORDER BY DocumentNo DESC";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			if (bySalesRep)
				pstmt.setInt(1, sessionUser.getAD_User_ID());
			else
				pstmt.setInt(1, sessionUser.getC_BPartner_ID());
			
			
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInvoice (ctx, rs, null));
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
		log.fine("#" + list.size());
		return list;
	}	//	getInvoices	
	

	
	public List<MInvoice> getInvoices(int C_BPartner_ID) {

		List<MInvoice> list = new ArrayList<MInvoice>();
		
		if (C_BPartner_ID == 0)
			C_BPartner_ID = sessionUser.getC_BPartner_ID();
		
		String sql = "SELECT * FROM C_Invoice WHERE C_BPartner_ID=? ORDER BY DocumentNo DESC " ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_BPartner_ID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new MInvoice (ctx, rs, null));
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
		log.fine("#" + list.size());

		return list;
	}	//	getOrders	
	
	

	
	

	
	
	private BigDecimal getFreightAmt(int M_Shipper_ID) {
		
		BigDecimal freight;
		// TNT
		if (M_Shipper_ID == 1000002)
			freight = new BigDecimal(12.9);
		// EXAPAQ
		if (M_Shipper_ID == 1000005)
			freight =  new BigDecimal(9.2);
		else
			freight =  new BigDecimal(7.5);
			
			return freight;
		
	}	
	
	
}

