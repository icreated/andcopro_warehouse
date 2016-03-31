/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.action;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import com.andcopro.util.Envs;
import com.andcopro.util.SecurityUtil;

@ManagedBean
@ApplicationScoped
public class Common {
	
	
	
	CLogger log = CLogger.getCLogger(Common.class);	
	
		
	
	public <T extends PO> T getPOById(String tableName, int id) {
		
		Query query = new Query(Envs.getCtx(), tableName, tableName+"_ID="+id,  null);
		return query.first();
		
	}	
	
	
	
	
	public List<PO> getTableList(String tableName) {
		Query query = new Query(Envs.getCtx(), tableName, "AD_Client_ID ="+Env.getAD_Client_ID(Envs.getCtx()), null);
		return query.list();
		
	}
	
	
	
	public List<PO> getTableList(String tableName, String whereClause) {
		
		Query query = new Query(Envs.getCtx(), tableName, whereClause, null);
		return query.list();
		
	}
	
	
	public List<SelectItem> getReferenceList(int AD_Reference_ID) {
		
		String sql = "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=? AND isActive='Y' ORDER BY Name"; 
		List<SelectItem> list = new ArrayList<SelectItem>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, null);
				pstmt.setInt (1, AD_Reference_ID);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					list.add(new SelectItem(rs.getString(1),rs.getString(2)));
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
	
	
	public String getSideMark(int C_Order_ID) {
		
		return DB.getSQLValueString(null, "SELECT so.POReference FROM C_Order po INNER JOIN C_Order so ON po.C_Order_ID = so.Link_Order_Id WHERE po.C_Order_ID = ?", C_Order_ID);
		
		
	}
	
	
	public void checkAlreadyLoggedin() throws IOException {
		
	    if (SecurityUtil.isNotGranted("ROLE_USER")) {
	        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        ec.redirect(ec.getRequestContextPath() + "/index.jsp");
	    }
	}

}
