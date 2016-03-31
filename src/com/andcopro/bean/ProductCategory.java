/******************************************************************************
 * Product: Compiere ERP & CRM Smart Business Solution                        *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package com.andcopro.bean;

import java.math.BigDecimal;
import java.net.URLEncoder;

import org.compiere.util.CLogger;

/**
 *  Price List Product
 *
 *  @author Jorg Janke
 *  @version $Id: PriceListProduct.java,v 1.2 2006/07/30 00:53:21 jjanke Exp $
 */
public class ProductCategory implements Comparable<ProductCategory>
{
	/**
	 * 	Price List Product.
	 * 	@param M_Product_ID product
	 * 	@param value value
	 * 	@param name name
	 * 	@param description descriprion
	 * 	@param help help
	 * 	@param documentNote document note
	 * 	@param imageURL image
	 * 	@param descriptionURL description
	 * 	@param price price
	 * 	@param uomName uom
	 * 	@param uomSymbol uom
	 */
	public ProductCategory (int M_Product_Category_ID, String name, String description, String url, int picto_ID, BigDecimal price)
	{
		//
		m_Product_Category_ID = M_Product_Category_ID;
		m_name = name;
		m_description = description;
		m_url = url;
		m_picto_ID = picto_ID;
		m_price = price;
			
	}	//	PriceListProduct

	/**	Attribute Name				*/
	public static final String		NAME = "ProductCategory";
	/**	Logging						*/
	private CLogger			log = CLogger.getCLogger(getClass());

	private int 			m_Product_Category_ID;
	private String 			m_name;
	private String 			m_description;
	private String 			m_imageURL;

	private BigDecimal		m_price;
	private String			m_url;
	private int				m_picto_ID;
	private String			m_uomName;
	private String			m_uomSymbol;
	private String 			universName;
	private int 			universID;
	private int 			imageID;
	/**
	 * 	String Representation
	 * 	@return info
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("ProductCategory[");
		sb.append(m_Product_Category_ID).append("-").append(m_name)
			.append("-").append(m_price)
			.append("]");
		return sb.toString();
	}	//	toString

	/*************************************************************************/

	/**
	 * 	Get Product IO
	 * 	@return	M_Product_ID
	 */
	public int getId()
	{
		return m_Product_Category_ID;
	}
	
	public void setId(int id) {
		
		m_Product_Category_ID = id;
	}
	/**
	 * 	Get Name
	 * 	@return name
	 */
	public String getName()
	{
		return m_name;
	}
	public String getDescription()
	{
		return m_description;
	}

	public BigDecimal getPrice()
	{
		return m_price;
	}

	public String getUrl() {

		return m_url;
	}
	
	
	public int getPictoID() {
		return m_picto_ID;
	}

	public void setPictoID(int pictoID) {
		m_picto_ID = pictoID;
	}	
	
	public void setUomName(String name) {
		m_uomName = name;
	}

	public void setUomSymbol(String symbol) {
		m_uomSymbol = symbol;
	}

	public String getUomName()
	{
		return m_uomName;
	}
	public String getUomSymbol()
	{
		return m_uomSymbol;
	}

	public String getUniversName() {
		return universName;
	}

	public void setUniversName(String universName) {
		this.universName = universName;
	}

	public int getUniversID() {
		return universID;
	}

	public void setUniversID(int universID) {
		this.universID = universID;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	

	@Override
	public int compareTo(ProductCategory o) {
		if (getName().equals(o.getName()))
			return 0;
		else return getName().compareTo(o.getName()) > 0 ? 1 : -1;
	}
	
	
	
	
}	//	PriceListProduct
