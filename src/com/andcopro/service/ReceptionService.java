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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MRequest;
import org.compiere.model.MRequestType;
import org.compiere.model.MSequence;
import org.compiere.model.MStore;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

import com.andcopro.bean.LabelBean;
import com.andcopro.bean.Locator;
import com.andcopro.bean.Order;
import com.andcopro.bean.ReceptionBean;
import com.andcopro.bean.WUser;
import com.andcopro.util.Envs;

public class ReceptionService  extends AService {

	

	CLogger log = CLogger.getCLogger(ReceptionService.class);	
	
	 ReceptionService(Properties ctx, WUser sessionUser, MStore webStore) {
	    super(ctx, sessionUser, webStore);
	  }
	 
	 
	public List<KeyNamePair> getWarehouses() {	 
		
		KeyNamePair[] knp = DB.getKeyNamePairs("SELECT M_Warehouse_ID, Name FROM M_Warehouse WHERE AD_Org_ID = ?", false, Env.getAD_Org_ID(Envs.getCtx()));
		return Arrays.asList(knp);
	}
	 
	 
	 
	 
	
	public List<MBPartner> getVendors() {
		
		String sql = "SELECT * FROM C_BPartner WHERE AD_Client_ID = ? AND IsVendor='Y' AND IsActive='Y' ORDER BY Name"; 
		List<MBPartner> list = new ArrayList<MBPartner>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt(1, Env.getAD_Client_ID(Envs.getCtx()));
			rs = pstmt.executeQuery ();
			
			while (rs.next ())
				list.add(new MBPartner(ctx,rs, null));
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
	
	
	
	public List<MOrder> getPurchaseOrders(MBPartner bpartner, int M_Warehouse_ID) {
		
		
		String sql = "SELECT * FROM C_Order WHERE IsSOTrx='N' AND DocStatus IN ('CO') AND C_BPartner_ID=? ";
		if (M_Warehouse_ID > 0)
			sql +="AND M_Warehouse_ID = ? ";
		sql += "ORDER BY DocumentNo";
		
		List<MOrder> list = new ArrayList<MOrder>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt(1, bpartner.getC_BPartner_ID());
			if (M_Warehouse_ID > 0)
				pstmt.setInt(2, M_Warehouse_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add(new MOrder(ctx, rs, null));
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
	
	
	
	public MOrder getPurchaseOrder(String documentNo) {
		
		
		
		String sql = "SELECT * FROM C_Order WHERE IsSOTrx='N' AND DocumentNo LIKE ? AND DocStatus IN ('CO') " +
				"ORDER BY DocumentNO DESC"; 

		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MOrder retValue = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setString(1, "%"+documentNo+"%");
			rs = pstmt.executeQuery ();
			if (rs.next ())
				retValue = new MOrder(ctx, rs, null);
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
		return retValue;		
	}	
	
	
	
	
	public List<ReceptionBean> getOrderData (int C_Order_ID, boolean forInvoice)
	{

		log.config("C_Order_ID=" + C_Order_ID);
		MOrder order = new MOrder (ctx, C_Order_ID, null);      //  save

		List<ReceptionBean> list = new ArrayList<ReceptionBean>();
		StringBuffer sql = new StringBuffer("SELECT "
				+ "l.QtyOrdered-SUM(COALESCE(m.Qty,0)),"					//	1
				+ "CASE WHEN l.QtyOrdered=0 THEN 0 ELSE l.QtyEntered/l.QtyOrdered END,"	//	2
				+ " l.C_UOM_ID, COALESCE(uom.UOMSymbol, uom.Name),"			//	3..4
				+ " p.M_Locator_ID, loc.Value, " // 5..6
				+ " COALESCE(l.M_Product_ID,0), COALESCE(p.Name,c.Name), " //	7..8
				+ " po.VendorProductNo, " // 9
				+ " l.C_OrderLine_ID,l.Line, "	//	10..11
				+ " l.Description, ll.C_Order_ID, c.C_Charge_ID "
				+ "FROM C_OrderLine l"
				+ " LEFT OUTER JOIN M_Product_PO po ON (l.M_Product_ID = po.M_Product_ID AND l.C_BPartner_ID = po.C_BPartner_ID) "
				+ " LEFT OUTER JOIN M_MatchPO m ON (l.C_OrderLine_ID=m.C_OrderLine_ID AND ");
		sql.append(forInvoice ? "m.C_InvoiceLine_ID" : "m.M_InOutLine_ID");
		sql.append(" IS NOT NULL)")
		.append(" LEFT OUTER JOIN M_Product p ON (l.M_Product_ID=p.M_Product_ID)"
				+ " LEFT OUTER JOIN M_Locator loc on (p.M_Locator_ID=loc.M_Locator_ID)"
				+ " LEFT OUTER JOIN C_Charge c ON (l.C_Charge_ID=c.C_Charge_ID)"
				+ " LEFT OUTER JOIN C_OrderLine ll ON (ll.C_OrderLine_ID=l.Link_OrderLine_ID)");
		if (Env.isBaseLanguage(ctx, "C_UOM"))
			sql.append(" LEFT OUTER JOIN C_UOM uom ON (l.C_UOM_ID=uom.C_UOM_ID)");
		else
			sql.append(" LEFT OUTER JOIN C_UOM_Trl uom ON (l.C_UOM_ID=uom.C_UOM_ID AND uom.AD_Language='")
			.append(Env.getAD_Language(ctx)).append("')");
		//
		sql.append(" WHERE l.C_Order_ID=? AND l.QtyDelivered < l.QtyOrdered "			//	#1
				+ "GROUP BY l.QtyOrdered,CASE WHEN l.QtyOrdered=0 THEN 0 ELSE l.QtyEntered/l.QtyOrdered END, "
				+ "l.C_UOM_ID,COALESCE(uom.UOMSymbol,uom.Name), p.M_Locator_ID, loc.Value, po.VendorProductNo, "
				+ "l.M_Product_ID,COALESCE(p.Name,c.Name), l.Line,l.C_OrderLine_ID, l.Description, ll.C_Order_ID,"
				+ "c.C_Charge_ID "
				+ "ORDER BY l.Line");
		//

		log.finer(sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ReceptionBean bean = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, C_Order_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				bean = new ReceptionBean();
				bean.setSelected(false);     //  0-Selection
				
				BigDecimal qtyOrdered = rs.getBigDecimal(1);
				BigDecimal multiplier = rs.getBigDecimal(2);
				BigDecimal qtyEntered = qtyOrdered.multiply(multiplier);
				bean.setQty(qtyEntered);  //  1-Qty
				
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(4).trim());
				bean.setUom(pp);                           //  2-UOM
				pp = new KeyNamePair(rs.getInt(7), rs.getString(8));
				bean.setProduct(pp);                           //  4-Product
				bean.setVendorProductNo(rs.getString(9));				// 5-VendorProductNo
				pp = new KeyNamePair(rs.getInt(10), rs.getString(11));
				bean.setOrderLine(pp);                          //  6-OrderLine
				bean.setDescription(rs.getString(12));
				bean.setOrderId(rs.getInt(13));
				bean.setCharge(rs.getInt(14) > 0);
				list.add(bean);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
			//throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return list;
	}   //  LoadOrder	
	
	
	
	
	public Map<Integer,Locator> getFreeLocators(int M_Warehouse_ID) {
			
			String trxName = null;
			Map<Integer, Locator> map = new HashMap<Integer, Locator>();
	
			String sql = "SELECT DISTINCT l.M_Locator_ID, l.IsDefault, l.Value, SUM(s.QtyOnHand) > 0, l.PriorityNo  FROM M_Locator l    " +
					"LEFT JOIN M_StorageOnHand s ON l.M_Locator_ID = s.M_Locator_ID " +
					"WHERE M_Warehouse_ID = ? AND l.isActive='Y' " +
					"GROUP BY l.M_Locator_ID, l.Value,l.IsDefault, l.PriorityNo  " +
					"ORDER BY l.IsDefault DESC, l.PriorityNo " ;
	;
	
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, trxName);
				pstmt.setInt (1, M_Warehouse_ID);
				rs = pstmt.executeQuery ();
				while (rs.next ()) {
					map.put(rs.getInt(1),new Locator(rs.getInt(1), rs.getString(2).equals("Y"),rs.getString(3),
							rs.getBoolean(4),rs.getInt(5)));
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
			
			return map;
		}


	public MInOut createReception(int M_Locator_ID,  MOrder po, List<ReceptionBean> selectedItems, boolean isInDispute) {

		
		// Workaround: MSequence getting often with transactions -  Where: while locking tuple (33,33) in relation "ad_sequence"
		String trxName = po.get_TrxName();
		po.set_TrxName(null);
		MInOut inout = new MInOut(po, 0, new Timestamp(System.currentTimeMillis()));
		inout.setIsInDispute(isInDispute);
		inout.save();
		po.set_TrxName(trxName);
		inout.set_TrxName(trxName);
		

		for (ReceptionBean line : selectedItems) {
			
				if (line.isInDispute())
					continue;
				
				BigDecimal QtyEntered = line.getQty();
				int C_UOM_ID = line.getUom().getKey();
				int M_Product_ID = line.getProduct().getKey();
				int C_OrderLine_ID = line.getOrderLine().getKey();

				int precision = 2;
				if (M_Product_ID != 0)
				{
					MProduct product = MProduct.get(ctx, M_Product_ID);
					precision = product.getUOMPrecision();
				}
				QtyEntered = QtyEntered.setScale(precision, BigDecimal.ROUND_HALF_DOWN);
				//
				log.fine("Line QtyEntered=" + QtyEntered+ ", Product=" + M_Product_ID + ", OrderLine=" + C_OrderLine_ID);


				//	Create new InOut Line
				MInOutLine iol = new MInOutLine (inout);
				iol.setM_Product_ID(M_Product_ID, C_UOM_ID);	//	Line UOM
				iol.setQty(QtyEntered);							//	Movement/Entered
				//
				MOrderLine ol = null;
				if (C_OrderLine_ID != 0) {
					iol.setC_OrderLine_ID(C_OrderLine_ID);
					ol = new MOrderLine (ctx, C_OrderLine_ID, null);
					if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0)
					{
						iol.setMovementQty(QtyEntered
								.multiply(ol.getQtyOrdered())
								.divide(ol.getQtyEntered(), 12, BigDecimal.ROUND_HALF_UP));
						iol.setC_UOM_ID(ol.getC_UOM_ID());
					}
					iol.setM_AttributeSetInstance_ID(ol.getM_AttributeSetInstance_ID());
					iol.setDescription(ol.getDescription());
					//
					iol.setC_Project_ID(ol.getC_Project_ID());
					iol.setC_ProjectPhase_ID(ol.getC_ProjectPhase_ID());
					iol.setC_ProjectTask_ID(ol.getC_ProjectTask_ID());
					iol.setC_Activity_ID(ol.getC_Activity_ID());
					iol.setC_Campaign_ID(ol.getC_Campaign_ID());
					iol.setAD_OrgTrx_ID(ol.getAD_OrgTrx_ID());
					iol.setUser1_ID(ol.getUser1_ID());
					iol.setUser2_ID(ol.getUser2_ID());

				}

				//	Charge
				if (M_Product_ID == 0)
				{
					if (ol != null && ol.getC_Charge_ID() != 0)			//	from order
						iol.setC_Charge_ID(ol.getC_Charge_ID());
				}
				// Set locator
				iol.setM_Locator_ID(M_Locator_ID);
				if (!iol.save())
					log.log(Level.SEVERE, "Line NOT created #" + line);
		}
		
		return inout;
	}
	
	
	public MInvoice createInvoice(MInOut ship) {
		
		String trxName = ship.get_TrxName();
		ship.set_TrxName(null);
		MInvoice invoice = new MInvoice (ship, null);
		if (!invoice.save())
			throw new IllegalArgumentException("Cannot save Invoice");
		
		ship.set_TrxName(trxName);
		invoice.set_TrxName(trxName);
		
		MInOutLine[] shipLines = ship.getLines(false);
		for (int i = 0; i < shipLines.length; i++)
		{
			MInOutLine sLine = shipLines[i];
			MInvoiceLine line = new MInvoiceLine(invoice);
			line.setShipLine(sLine);
			if (sLine.sameOrderLineUOM())
				line.setQtyEntered(sLine.getQtyEntered());
			else
				line.setQtyEntered(sLine.getMovementQty());
			line.setQtyInvoiced(sLine.getMovementQty());
			if (!line.save())
				throw new IllegalArgumentException("Cannot save Invoice Line");
		}		
		
		return invoice;
		
	}

	public MRequest createNewRequest(MOrder order, MInOut inout, MInvoice invoice, int R_RequestType_ID, int SalesRep_ID, String summary) {
		
		
		
		MRequest req = new MRequest(ctx, SalesRep_ID, R_RequestType_ID, summary, true, null);
		req.setC_BPartner_ID(order.getC_BPartner_ID());
		req.setAD_User_ID(order.getAD_User_ID());
		req.setC_Order_ID(order.getC_Order_ID());
		if (inout != null)
			req.setM_InOut_ID(inout.getM_InOut_ID());
		if (invoice != null)
			req.setC_Invoice_ID(invoice.getC_Invoice_ID());
		req.setC_Project_ID(order.getC_Project_ID());
		req.setPriority("3");
		req.setConfidentialType(MRequest.CONFIDENTIALTYPE_PartnerConfidential);
		MRequestType requestType = MRequestType.get(ctx, R_RequestType_ID);
		req.setR_Status_ID(requestType.getDefaultR_Status_ID());

		//
		StringBuffer sb = new StringBuffer();
		sb.append("From:").append(sessionUser.getUsername());
		req.setLastResult(sb.toString());
		//
		if (!req.save())
		{
			log.log(Level.SEVERE, "New Request NOT saved");
			return null;
		}		
		return req;
	}	
	

	/*		
	public String printLabel(String processValue, int Record_ID) {
	    String info = null;
	    
	    int AD_Process_ID = DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Process WHERE Value LIKE ?", processValue);
	    
	    MPInstance instance = new MPInstance(this.ctx, AD_Process_ID, 0);
	    if (!instance.save())
	    {
	      info = Msg.getMsg(this.ctx, "ProcessNoInstance");
	      return info;
	    }
	    ProcessInfo pi = new ProcessInfo("Print", AD_Process_ID);
	    pi.setAD_Client_ID(Env.getAD_Client_ID(this.ctx));
	    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
	    pi.setRecord_ID(Record_ID);
	    
	    MProcess process = MProcess.get(this.ctx, AD_Process_ID);
	    
	    String trxName = Trx.createTrxName("LABEL");
	    Trx trx = Trx.get(trxName, true);
	    trx.commit();
	    
	    AEnv.executeAsyncDesktopTask(new Runnable()
	    {
	      public void run()
	      {
	        Jasperreport report = new Jasperreport();
	        report.setSrc("/Adempiere/reports/text.jasper");
	        report.setType("pdf");
	      }
	    });
	    System.out.println("33333333333333333333333");
	    
	    System.out.println("444444444444444444444444444");
	    
	    return "";
	  }	
	
	
	
	
	
	  private void printDirectly()
	  {
	    AEnv.executeAsyncDesktopTask(new Runnable()
	    {
	      public void run()
	      {
	        MShipperLabels lbl = new MShipperLabels(StockService.this.ctx, 1000000, null);
	        MAttachmentEntry[] entries = lbl.getAttachment().getEntries();
	        List<byte[]> list = new ArrayList();
	        if ((entries != null) && (entries.length > 0))
	        {
	          MAttachmentEntry[] arrayOfMAttachmentEntry1;
	          int j = (arrayOfMAttachmentEntry1 = entries).length;
	          for (int i = 0; i < j; i++)
	          {
	            MAttachmentEntry entry = arrayOfMAttachmentEntry1[i];
	            if (entry.getName().startsWith("shipping_label")) {
	              list.add(entry.getData());
	            }
	          }
	        }
	        LabelAppletWindow law = new LabelAppletWindow(list);
	      }
	    });
	  }
	

	private void printDirectly(final Page page) {
		
		AEnv.executeAsyncDesktopTask(new Runnable() {
			@Override
			public void run() {
				
				MShipperLabels lbl = new MShipperLabels(ctx, 1000000, null);
				MAttachmentEntry[] entries = lbl.getAttachment().getEntries();
				List<byte[]> list = new ArrayList<byte[]>();
				if (entries != null && entries.length > 0) 
				{
					for (MAttachmentEntry entry : entries) 
					{
						if (entry.getName().startsWith("shipping_label"))
							list.add(entry.getData());
					}
				}
				
				
				LabelAppletWindow law = new LabelAppletWindow(list);
				law.setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
				law.setPage(page);
				law.doModal();
			}
		});		
		
	}
	
	*/	
	
	
}
