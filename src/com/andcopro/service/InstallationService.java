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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MPreference;
import org.compiere.model.MProcess;
import org.compiere.model.MProduct;
import org.compiere.model.MRequest;
import org.compiere.model.MStatus;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MStore;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

import com.andcopro.bean.InstallBean;
import com.andcopro.bean.OrderLine;
import com.andcopro.bean.ResourceBean;
import com.andcopro.bean.WUser;
import com.andcopro.util.Envs;

public class InstallationService extends AService {
	
	
	public static final String REQUESTTYPE_PURCHASE= "Purchase";
	public static final String REQUESTTYPE_INSTALL= "Install";

	
	public static final String STATUS_VERIFY_PAYMENT = "Verify Payment";	
	public static final String STATUS_INSTALL_SCHEDULED = "Install Scheduled";	
	public static final String STATUS_INSTALLATION = "Installation";
	public static final String STATUS_INSTALL_COMPLETE = "Install Complete";
	public static final String STATUS_SCHEDULE_MEASURE = "Schedule Measure";
	public static final String STATUS_SCHEDULE_PO = "Schedule PO";
	public static final String STATUS_ASSIGN_CREW = "Assign Crew";
	public static final String STATUS_REDO = "Redo";
	
	CLogger log = CLogger.getCLogger(InstallationService.class);		
	
	
	InstallationService(Properties ctx, WUser sessionUser, MStore webStore) {
		
		super(ctx, sessionUser, webStore);
	}
	
	
	public List<OrderLine> getOrderLines2(int M_InOut_ID) {
		
		MOrder so = null;
		MInOut inout = new MInOut(ctx, M_InOut_ID, null);
//		System.out.println("RECEPTION="+inout.getDocumentNo());
		MOrder po = new MOrder(ctx, inout.getC_Order_ID(), null);
//		System.out.println("PO="+po.getDocumentNo());
		if (po.getLink_Order_ID() > 0)
			so = new MOrder(ctx, po.getLink_Order_ID(), null);
		else {
			int C_Order_ID = DB.getSQLValue(null, "SELECT C_Order_ID FROM C_Order WHERE DocumentNo LIKE ?", po.getPOReference());
			so = new MOrder(ctx, C_Order_ID, null);
		}

		
		List<OrderLine> list = new ArrayList<OrderLine>();
		MOrderLine soLine = null;
		for (MInOutLine ioLine : inout.getLines()) {
				MOrderLine poLine = new MOrderLine(ctx, ioLine.getC_OrderLine_ID(), null);
				if (poLine.getLink_OrderLine_ID() > 0)
					soLine = new MOrderLine(ctx, poLine.getLink_OrderLine_ID(), null);
				else {
					for(MOrderLine l : so.getLines()) {
						if (poLine.getM_Product_ID() == l.getM_Product_ID() 
								&& poLine.getM_AttributeSetInstance_ID() == l.getM_AttributeSetInstance_ID()) {
							soLine = new MOrderLine(ctx, l.getC_OrderLine_ID(), null);
							break;
						}
					}

				}
				
				
			MProduct product = soLine.getProduct();
//			BigDecimal qtyOnHand = MStorageOnHand.getQtyOnHand(soLine.getM_Product_ID(), po.getM_Warehouse_ID(), soLine.getM_AttributeSetInstance_ID(), null);
//			int M_Locator_ID = MStorageOnHand.getM_Locator_ID(po.getM_Warehouse_ID(), soLine.getM_Product_ID(), soLine.getM_AttributeSetInstance_ID(), soLine.getQtyOrdered(), null);
//			MLocator loc = MLocator.get(ctx, M_Locator_ID);
			
			list.add(new OrderLine(soLine.getC_Order_ID(), product.getName(), product.getDescription(),soLine.getQtyOrdered(),
					ioLine.getMovementQty(), soLine.get_ValueAsString("Floorplan"), new BigDecimal(soLine.get_ValueAsInt("Window_Number")),
					new BigDecimal(soLine.get_ValueAsString("Width")),new BigDecimal(soLine.get_ValueAsString("Length"))));
		}
		
		return list;
		
	}
	
	
	public List<OrderLine> getOrderLines(int M_InOut_ID) {

/*
		String sql = "SELECT ol.C_OrderLine_ID, p.Name, p.Description, ol.QtyOrdered, ol.LineNetAmt, SUM(s.QtyOnHand), "
				+ "ol.Floorplan, ol.Window_Number, ol.Width, ol.length " +
				"FROM C_OrderLine ol " +
				"INNER JOIN C_Order o ON ol.C_Order_ID = o.C_Order_ID " +
				"INNER JOIN C_Order po ON o.Link_Order_ID = po.C_Order_ID " +
				"INNER JOIN M_Product p ON ol.M_Product_ID = p.M_Product_ID " +
				"LEFT JOIN M_StorageOnHand s ON s.M_Product_ID = p.M_Product_ID " +
				"WHERE ol.C_Order_id = ? AND " +
				"GROUP BY ol.C_OrderLine_ID, p.Name, p.Description, ol.QtyOrdered, ol.LineNetAmt " ;
		*/
		
		String sql = "SELECT DISTINCT ol.C_OrderLine_ID, p.Name, p.Description, ol.QtyOrdered, ol.LineNetAmt, s.QtyOnHand,  " +
				"ol.Floorplan, ol.Window_Number, ol.Width, ol.length, l.value " +
				"FROM C_OrderLine ol  " +
				"INNER JOIN C_OrderLine pol ON pol.Link_OrderLine_ID = ol.C_OrderLine_ID " +
				"INNER JOIN M_InOutLine iol ON iol.C_OrderLine_ID = pol.C_OrderLine_ID " +
				"INNER JOIN M_Product p ON ol.M_Product_ID = p.M_Product_ID  " +
				"LEFT JOIN M_StorageOnHand s ON (s.M_Product_ID = p.M_Product_ID AND pol.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID)  " +
				"INNER JOIN M_Locator l ON s.M_Locator_ID = l.M_Locator_ID " +
				"WHERE iol.M_InOut_ID = ?" ;
		
		
		
		List<OrderLine> list = new ArrayList<OrderLine>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_InOut_ID);	
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new OrderLine(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBigDecimal(4), rs.getBigDecimal(6),
						rs.getString(7), rs.getBigDecimal(8), rs.getBigDecimal(9), rs.getBigDecimal(10)));
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
	
	
	public List<ResourceBean> getResourceAssignements(int C_Order_ID) {

		String sql = "SELECT a.AssignDateFrom, r.Name FROM S_ResourceAssignment a "
				+ "INNER JOIN S_Resource r ON a.S_Resource_ID =  r.S_Resource_ID "
				+ "WHERE a.C_Order_ID = ?";
		List<ResourceBean> list = new ArrayList<ResourceBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_Order_ID);	
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new ResourceBean(rs.getDate(1), rs.getString(2)));
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
	
	

	public List<InstallBean> getMeasures() {
		
		
		String sql = "SELECT req.R_Request_ID, req.Summary, bp.Name, u.phone, " +
				"loc.Address1,loc.Address2,loc.City,loc.Postal,c.CountryCode, ord.DocumentNo, ord.C_Order_ID, req.DocumentNo  " +
				"FROM R_Request req   " +
				"INNER JOIN C_Order ord ON req.C_Order_ID = ord.C_Order_ID " +
				"INNER JOIN C_BPartner_Location bpl ON bpl.C_BPartner_Location_ID=ord.C_BPartner_Location_ID " +
				"LEFT JOIN AD_User u ON u.AD_User_ID=ord.AD_User_ID " +
				"INNER JOIN R_Status reqs ON reqs.R_Status_ID = req.R_Status_ID  " +
				"INNER JOIN R_RequestType reqt ON reqt.R_RequestType_ID = req.R_RequestType_ID " +
				"INNER JOIN C_BPartner bp ON req.C_BPartner_ID=bp.C_BPartner_ID " +
				"INNER JOIN C_Location loc  ON bpl.C_Location_ID=loc.C_Location_ID " +
				"INNER JOIN C_Region r  ON r.C_Region_ID=loc.C_Region_ID " +
				"INNER JOIN C_Country c  ON c.C_Country_ID=loc.C_Country_ID " +
				"WHERE reqt.name IN ('Purchase') AND reqs.name IN ('Schedule Measure') ORDER BY req.DocumentNo";

		// AND s.Name LIKE 'Installation'
		List<InstallBean> list = new ArrayList<InstallBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);	
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new InstallBean(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), 
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getInt(11), rs.getString(12)));
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

	
	
	public List<InstallBean> getRecapInstallation() {
		
		
		String sql = "SELECT DISTINCT io.M_InOut_ID, o.DocumentNo, io.DocumentNo, req.DocumentNo, r.Name, ra.AssignDateFrom, ra.AssignDateTo,s.Name,   " +
			"io.DocStatus, req.R_Request_ID, ra.S_ResourceAssignment_ID, o.POReference, ra.Name FROM M_InOut io  " +
			"INNER JOIN C_Order o ON io.C_Order_ID = o.C_Order_ID " +
			"INNER JOIN S_ResourceAssignment ra ON o.C_Order_ID = ra.C_Order_ID  " +
			"LEFT JOIN R_Request req ON req.R_Request_ID IN  " +
			"(SELECT R_Request_ID FROM R_Request WHERE DocumentNo IN (SELECT substring(array_to_string(regexp_matches(raa.name, 'R#\\d+', 'gi'),''),3)  " +
			"FROM S_ResourceAssignment raa WHERE raa.S_ResourceAssignment_ID = ra.S_ResourceAssignment_ID)) AND req.M_InOutInstall_ID = io.M_InOut_ID " +
			"INNER JOIN R_Status s ON req.R_Status_ID  = s.R_Status_ID   " +
			"INNER JOIN S_Resource r ON ra.S_Resource_ID = r.S_Resource_ID   " +
			"WHERE (io.DocStatus IN ('DR','IP') AND s.Name LIKE 'Install_Scheduled') OR (io.DocStatus IN ('CO') AND s.Name LIKE 'Installation')   " +
			"ORDER BY o.DocumentNo DESC, io.DocumentNo " ;		
		

		List<InstallBean> list = new ArrayList<InstallBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				
				InstallBean install = new InstallBean(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getDate(6), rs.getDate(7), rs.getString(8), rs.getString(9), 
						rs.getInt(10), rs.getInt(11), rs.getString(12));
					list.add(install);
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
		
	}
	
	
	
	public void voidInstallation(int M_InOut_ID, String trxName) {
		
		MInOut ship = new MInOut(ctx, M_InOut_ID, trxName);
		ship.processIt(DocAction.ACTION_Void);
		ship.save();
	}
	
	
	
	
	public List<InstallBean> getInstallationToGenerate(int C_BPartner_ID, String M_Warehouse_ID, String requestTypeName, String statusName)  {
	//  Create SQL
		StringBuffer sql = new StringBuffer(
						"SELECT ord.C_Order_ID, ic.DocumentNo, bp.Name, u.phone," +
						"loc.Address1,loc.Address2,loc.City,loc.Postal,c.CountryCode," +
						"reqs.name, requ.Name, req.R_Request_ID, r.Name, io.M_InOut_ID, req.DocumentNo, ord.POReference, " +
						"po.C_Order_ID, po.DocumentNo, po.C_Order_ID " +
						"FROM M_InOut_Candidate_v ic  " +
						"INNER JOIN C_Order ord ON ic.C_Order_ID = ord.C_Order_ID " +
						"INNER JOIN R_Request req  ON ord.C_Order_ID=req.C_Order_ID  " +
						"LEFT JOIN M_InOut io ON io.M_InOut_ID = req.M_InOut_ID " +
						"INNER JOIN C_Order po  ON po.C_Order_ID=io.C_Order_ID  " +
						"INNER JOIN R_Status reqs ON reqs.R_Status_id = req.R_Status_id " +
						"INNER JOIN R_requesttype reqt ON reqt.r_requesttype_id = req.r_requesttype_id " +
						"LEFT JOIN AD_User requ  ON ord.SalesRep_ID=requ.AD_User_ID " +						
						"INNER JOIN C_BPartner bp ON ic.C_BPartner_ID=bp.C_BPartner_ID  " +
						"LEFT JOIN AD_User u ON u.AD_User_ID=ord.AD_User_ID  " +
						"INNER JOIN C_BPartner_Location bpl  ON ord.C_BPartner_Location_ID=bpl.C_BPartner_Location_ID  " +
						"INNER JOIN C_Location loc  ON bpl.C_Location_ID=loc.C_Location_ID  " +
						"INNER JOIN C_Region r  ON r.C_Region_ID=loc.C_Region_ID  " +
						"INNER JOIN C_Country c  ON c.C_Country_ID=loc.C_Country_ID  " +
						"WHERE bp.SoCreditStatus != 'S' AND reqt.name IN (%s) AND reqs.name IN (%s) " +
						"AND ic.AD_Client_ID=? ");

        if (M_Warehouse_ID != null)
            sql.append(" AND ic.M_Warehouse_ID IN (").append(M_Warehouse_ID).append(")");
        if (C_BPartner_ID > 0)
            sql.append(" AND ic.C_BPartner_ID=").append(C_BPartner_ID);

        sql.append(" ORDER BY ic.DocumentNo DESC");
        String sqlStr = String.format(sql.toString(), requestTypeName, statusName);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<InstallBean> list = new ArrayList<InstallBean>();
		try
		{
			pstmt = DB.prepareStatement (sqlStr, null);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));
			rs = pstmt.executeQuery ();
			while (rs.next ()) {
/*				
				String status = getPOStatus(rs.getInt(1));
					if (status.equals("CO"))
						style = "background-color:green;";
					else if (status.equals("IP"))
						style = "background-color:yellow;";
					else
						style = "background-color:red;";
*/				
				list.add(new InstallBean(rs.getInt(1), rs.getString(2),rs.getString(3), rs.getString(4),
						rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),
						rs.getString(10),rs.getString(11),null, isInStock(rs.getInt(1)), getResourceAssignements(rs.getInt(1)), 
						rs.getInt(12), rs.getString(13), rs.getInt(14),rs.getString(15),rs.getString(16), rs.getString(18),rs.getInt(19)));
			}

		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, sql.toString(), ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}        
        
        return list;
	}

	
	public String getPOStatus(int C_Order_ID) {
		
		// CO All Lines have been received
		// DR Not
		// IP certain products have been received
		
		String sql = "SELECT  " +
				"CASE sum(QtyOrdered) - sum(QtyDelivered) " +
				"  WHEN  0 THEN 'CO' " + 
				"  WHEN sum(QtyOrdered) THEN 'DR' " +
				"  ELSE 'IP' " +
				"END " +
				"FROM C_OrderLine WHERE C_Order_ID = ?";
		
		return DB.getSQLValueString(null, sql, C_Order_ID);
		
		
	}
	
	private boolean isInStock (int C_Order_ID) {
		
		
		String sql = "SELECT DISTINCT p.M_Product_ID, SUM(ol.QtyOrdered), ol.M_AttributeSetInstance_ID " +
				"FROM C_OrderLine ol " +
				"INNER JOIN M_Product p ON ol.M_Product_ID = p.M_Product_ID " +
				"LEFT JOIN M_StorageOnHand s ON s.M_Product_ID = p.M_Product_ID AND ol.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID " +
				"WHERE C_Order_ID = ? " +
				"GROUP BY p.M_Product_ID,s.QtyOnHand,ol.M_AttributeSetInstance_ID";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isInStock = true;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_Order_ID);	
			rs = pstmt.executeQuery();

			while (rs.next()) {
				
				BigDecimal qty = MStorageOnHand.getQtyOnHand(rs.getInt(1),Envs.getWebStore().getM_Warehouse_ID(), rs.getInt(3), null);
				isInStock = isInStock & (qty.compareTo(rs.getBigDecimal(2) == null ? BigDecimal.ZERO : rs.getBigDecimal(2)) >= 0);
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
		return isInStock;
		
	}
	
	
	public void updateStatusRequest(int R_Request_ID, int SalesRep_ID, String statusName, String summary,  String trxName) {
		
		Query query = new Query(ctx, MStatus.Table_Name, "Name LIKE ?",trxName);
		query.setParameters(statusName);
		MStatus status = query.first();

		if (status == null)
			return;
		MRequest request = new MRequest(ctx, R_Request_ID, trxName);
		// Init RequestType otherwise before save doesn't save
		request.getRequestType();
		request.setSummary(summary);
		request.setR_Status_ID(status.getR_Status_ID());
		if (SalesRep_ID > 0)
			request.setSalesRep_ID(SalesRep_ID);
		request.save();
		
	}
	
	
	public void updateRequests(int M_InOutInstall_ID, int SalesRep_ID, String statusName, String summary,  String trxName) {
		
		
		Query query = new Query(Envs.getCtx(), MRequest.Table_Name, "M_InOutInstall_ID="+M_InOutInstall_ID, trxName);
		List<MRequest> requests = query.list();
		for (MRequest request : requests) {

			Query statusQuery = new Query(ctx, MStatus.Table_Name, "Name LIKE ?",trxName);
			statusQuery.setParameters(statusName);
			MStatus status = statusQuery.first();
			if (status == null)
				continue;

			request.getRequestType();
			request.setSummary(summary);
			request.setR_Status_ID(status.getR_Status_ID());
			if (SalesRep_ID > 0)
				request.setSalesRep_ID(SalesRep_ID);
			request.save();			
		}		
		
		
	}
	
	
	
	public List<MUser> getUsersByRoleName(String roleName, String filter) {
		
	
	String sql = "SELECT u.* FROM AD_User_Roles ar  " +
			"INNER JOIN AD_User u ON ar.AD_User_ID = u.AD_User_ID " +
			"INNER JOIN AD_Role r ON ar.AD_Role_ID = r.AD_Role_ID " +
			"WHERE u.isActive='Y' AND r.Name LIKE ?";
	if (filter != null)
		sql += " AND UPPER(u.Name) LIKE UPPER(?)" ;
	
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	List<MUser> list = new ArrayList<MUser>();

	try
	{
		pstmt = DB.prepareStatement(sql, null);
		pstmt.setString(1, roleName);
		if (filter != null)
			pstmt.setString(2, filter+"%");
		rs = pstmt.executeQuery();

		while (rs.next()) {
			list.add(new MUser(ctx, rs,null));
			
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
	
	
	public int getPreferenceAttributeAsInt(String value, String trxName) {

		  int id = 0;
		  String userID = getPreferenceAttribute(value, trxName);
		  if (userID != null)
			  id = Integer.parseInt(userID);
		  return id;
	}
	
	public String getPreferenceAttribute(String value, String trxName) {
    	Query query = new Query(Envs.getCtx(), MPreference.Table_Name, "AD_User_ID = ? AND Value LIKE ?", trxName);
    	query.setParameters(Envs.getUserCredential().getAD_User_ID(), Envs.getUserCredential().getAD_User_ID()+"_"+value);
    	MPreference pref = query.first();
    	if (pref != null)
    		return pref.getAttribute();	
    	else
    		return null;
	}
	
	
	public void setPreferenceAttribute(String value, String attribute, String trxName) {
    	Query query = new Query(Envs.getCtx(), MPreference.Table_Name, "AD_User_ID = ? AND Value LIKE ?", trxName);
    	query.setParameters(Envs.getUserCredential().getAD_User_ID(), Envs.getUserCredential().getAD_User_ID()+"_"+value);
    	MPreference pref = query.first();
    	if (pref == null) {
    		pref = new MPreference(ctx, 0, trxName);
    		pref.setAD_User_ID(Envs.getUserCredential().getAD_User_ID());
    		pref.setValue(Envs.getUserCredential().getAD_User_ID()+"_"+value);
    	} 
    	pref.setAttribute(attribute);
        pref.save();	
	}	
	
	

	public String generateShipments(List<InstallBean> selection, String docActionSelected, int M_Warehouse_ID) {
		

		
		String info = "";

		String trxName = Trx.createTrxName("IOG");	
		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
		

		//	Prepare Process
//		int AD_Process_ID = 199;      // M_InOut_Generate - org.compiere.process.InOutGenerate 
		int AD_Process_ID = 1000005;  
		
		MPInstance instance = new MPInstance(ctx, AD_Process_ID, 0);
		if (!instance.save())
		{
			info = Msg.getMsg(ctx, "ProcessNoInstance");
			return info;
		}
		
		//insert selection
		StringBuffer insert = new StringBuffer();
		insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
		int counter = 0;
		for(InstallBean order: selection) {
			

			counter++;
			if (counter > 1)
				insert.append(" UNION ");
			insert.append("SELECT ");
			insert.append(instance.getAD_PInstance_ID());
			insert.append(", ");
			insert.append(order.getShipmentId());
			insert.append(" FROM DUAL ");
			
			if (counter == 1000) 
			{
				if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
				{
					String msg = "No Shipments";     //  not translated!
					log.config(msg);
					info = msg;
					trx.rollback();
					return info;
				}
				insert = new StringBuffer();
				insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
				counter = 0;
			}
			
		}
		if (counter > 0)
		{
			if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
			{
				String msg = "No Shipments";     //  not translated!
				log.config(msg);
				info = msg;
				trx.rollback();
				return info;
			}
		}
		

		//call process
		ProcessInfo pi = new ProcessInfo ("VInOutGen", AD_Process_ID);
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());

		//	Add Parameter - Selection=Y
		MPInstancePara ip = new MPInstancePara(instance, 10);
		ip.setParameter("Selection","Y");
		if (!ip.save())
		{
			String msg = "No Parameter added";  //  not translated
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}
		//Add Document action parameter
		ip = new MPInstancePara(instance, 20);
//		String docActionSelected = (String)docAction.getValue();
		ip.setParameter("DocAction", docActionSelected);
		if(!ip.save())
		{
			String msg = "No DocAction Parameter added";
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}
		//	Add Parameter - M_Warehouse_ID=x
		ip = new MPInstancePara(instance, 40);
		ip.setParameter("M_Warehouse_ID", M_Warehouse_ID);
		if(!ip.save())
		{
			String msg = "No Parameter added";  //  not translated
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}		
		
		
		
		
		MProcess process = MProcess.get(ctx, AD_Process_ID);
		boolean ok = process.processIt(pi, trx);
		if (!ok)
			return "ERROR:"+pi.getSummary();
		else
			return pi.getSummary();
	}	//	generateShipments




}
