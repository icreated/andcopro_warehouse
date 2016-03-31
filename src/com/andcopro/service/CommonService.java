/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.DBException;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MProduct;
import org.compiere.model.MStatus;
import org.compiere.model.MStore;
import org.compiere.model.MTax;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

import com.andcopro.bean.WUser;

public class CommonService  extends AService {

	

	CLogger log = CLogger.getCLogger(CommonService.class);
	
	
	

	CommonService(Properties ctx, WUser sessionUser, MStore webStore) {
		super(ctx, sessionUser, webStore);

	}	
	
	
	
	
	public int getM_PriceList_Version_ID (int M_PriceList_ID, Timestamp day) {
		String sql = "SELECT plv.M_PriceList_Version_ID, plv.Name, plv.Description, plv.ValidFrom " 	//	1..4
			+ "FROM M_PriceList_Version plv "
			+ "WHERE plv.M_PriceList_ID=?"		//	#1
			+ " AND plv.ValidFrom <=? "			//	#2
			+ "ORDER BY plv.ValidFrom DESC";
		
		PreparedStatement pstmt = null;
		int M_PriceList_Version_ID = 0;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_PriceList_ID);
			pstmt.setTimestamp(2, day);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				M_PriceList_Version_ID = rs.getInt(1);
			//  m_validFrom = rs.getTimestamp(4);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "getM_PriceList_Version_ID", e);
		}

		return M_PriceList_Version_ID;
	}	//	getM_PriceList_Version_ID	
	
	
	
	

	
	public List<String> getReferenceList(int AD_Reference_ID) {
		
		String sql = "SELECT Value FROM AD_Ref_List WHERE AD_Reference_ID=?"; 
		List<String> list = new ArrayList<String>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, null);
				pstmt.setInt (1, 214);
//				pstmt.setString(2, Value);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					list.add(rs.getString(1));
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
		
	
	
	public MProduct getProductByValue(String value, String trxName)
	{
		final String whereClause = "Value=?";
		Query q = new Query(ctx, MProduct.Table_Name, whereClause, trxName);
		q.setParameters(value).setClient_ID();
		return(q.firstOnly());
	}
	

	public MOrder getOrderByLink_Order_ID(int C_Order_ID, String trxName)
	{
		final String whereClause = "Link_Order_ID=?";
		Query q = new Query(ctx, MOrder.Table_Name, whereClause, trxName);
		q.setParameters(C_Order_ID).setClient_ID();
		return(q.firstOnly());
	}	
	
	
	public MStatus getStatusByValue(String value, String trxName)
	{
		final String whereClause = "Value=?";
		Query q = new Query(ctx, MStatus.Table_Name, whereClause, trxName);
		q.setParameters(value).setClient_ID();
		return(q.firstOnly());
	}
	
	public MDocType getDocTypeByDescription(String description, String trxName) {
		
		final String whereClause = "Description LIKE ?";
		Query q = new Query(ctx, MDocType.Table_Name, whereClause, trxName);
		q.setParameters(description).setClient_ID();		
		return q.firstOnly();
	}
	
	
	
	public MOrder getQuoteFromOrder(String documentNo, String trxName)
	{
		final String whereClause = "POReference LIKE ?";
		Query q = new Query(ctx, MOrder.Table_Name, whereClause, trxName);
		q.setParameters(documentNo).setClient_ID();
		
		MOrder po = null;
		try {
			po = q.firstOnly();
		} catch (DBException e) {
		}
		
		return po;
	}	
	
	public MTax getExemptedTax() {
		
		int taxIds[] = PO.getAllIDs(MTax.Table_Name, "Name LIKE 'No Tax' AND IsTaxExempt = 'Y'", null);
		int taxId = 0;
		if (taxIds.length > 0)
			return MTax.get(ctx, taxIds[0]);

		return null;
	}
	
	public BigDecimal calculateDiscountedPrice(BigDecimal priceList, BigDecimal discount) {
			
		return priceList.subtract(
				discount.multiply(priceList)
				.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
	
	}
	
}
