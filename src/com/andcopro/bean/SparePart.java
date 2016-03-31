package com.andcopro.bean;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SparePart")
@XmlAccessorType(XmlAccessType.FIELD)
public class SparePart {


	@XmlAttribute(name = "productId")
	private int productID = 0;
	@XmlAttribute(name = "name")
	private	String name = null;
	@XmlAttribute(name = "bpartnerName")
	private String nameBPartner = null;
	@XmlAttribute(name = "descirption")
	private String description = null;
	@XmlAttribute(name = "categoryName")
	private String categoryName = null;
	@XmlAttribute(name = "descriptionUrl")
	private String descriptionURL = null;
	@XmlAttribute(name = "price")
	private BigDecimal priceStd = null;
	@XmlAttribute(name = "line")
	private int	line = 0;
	@XmlAttribute(name = "bomQty")
	private int bomQty = 1;
	@XmlAttribute(name = "lineOption")
	private String lineOption = null;
	@XmlAttribute(name = "imageId")
	private int imageID;
	@XmlAttribute(name = "qtyOnHand")
	private int qtyOnHand;

	
	public SparePart() {
		
	}
	
	
	/**
	 * @param productID
	 * @param name
	 * @param description
	 * @param categoryName
	 * @param descriptionURL
	 * @param priceStd
	 * @param line
	 * @param bomQty
	 * @param lineOption
	 * @param imageID
	 */
	public SparePart(int productID, String name, String description, String categoryName, String descriptionURL, BigDecimal priceStd, int line, int bomQty, String lineOption, int imageID, int qtyOnHand, String nameBPartner) {
		super();
		this.productID = productID;
		this.name = name;
		this.description = description;
		this.categoryName = categoryName;
		this.descriptionURL = descriptionURL;
		this.priceStd = priceStd;
		this.line = line;
		this.bomQty = bomQty;
		this.lineOption = lineOption;
		this.imageID = imageID;
		this.qtyOnHand = qtyOnHand;
		this.nameBPartner = nameBPartner;
	}

	/**
	 * @return the bomQty
	 */
	public int getBomQty() {
		return bomQty;
	}

	/**
	 * @return the QtyOnHand
	 */
	public int getQtyOnHand() {
		return qtyOnHand;
	}
	
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the descriptionURL
	 */
	public String getDescriptionURL() {
		return descriptionURL;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the lineOption
	 */
	public String getLineOption() {
		return lineOption;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nameBPartner
	 */
	public String getNameBPartner() {
		return nameBPartner;
	}
	
	/**
	 * @return the priceStd
	 */
	public BigDecimal getPriceStd() {
		return priceStd;
	}

	/**
	 * @return the productID
	 */
	public int getProductID() {
		return productID;
	}
	
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("SparePart[id=");
		sb.append(productID).append(", name=").append(name)
			.append(", price=").append(priceStd)
			.append(", desc=").append(description)
			.append("]");
		return sb.toString();
	}	//	toString
	
	
}

