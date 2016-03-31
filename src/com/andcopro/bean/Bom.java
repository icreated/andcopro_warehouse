package com.andcopro.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Bom")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bom  extends SparePart {
	
	@XmlElement(name = "spareParts")
	private List<SparePart> spareParts = new ArrayList<SparePart>();
	
	public Bom() {}
	
	
	public Bom(int productID, String name, String description, String categoryName, String descriptionURL, BigDecimal priceStd, int line, int bomQty, String lineOption, int imageID, int qtyOnHand, String nameBPartner) {

		super(productID, name, description, categoryName, descriptionURL, priceStd, line, bomQty, lineOption, imageID, qtyOnHand, nameBPartner);
	}

	public List<SparePart> getSpareParts() {
		return spareParts;
	}
	
	public void setSpareParts(List<SparePart> spareParts) {
		this.spareParts = spareParts;
	}


	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("Bom[id=");
		sb.append(getProductID()).append(", name=").append(getName())
			.append(", size =").append(getSpareParts().size())
			.append("]");
		return sb.toString();
	}	//	toString

}
