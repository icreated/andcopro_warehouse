package com.andcopro.converter;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.compiere.model.MBPartner;
import org.compiere.model.MOrder;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesConverter("orderConverter")
public class OrderConverter implements Converter {
	 

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) throws ConverterException {
		
		
        if(value != null && value.trim().length() > 0) {
            try {
            	
        		SelectOneMenu som = (SelectOneMenu)component;
        		for(UIComponent child : som.getChildren()) {
        			if (child instanceof UISelectItems) {
        				@SuppressWarnings("unchecked")
        				List<MOrder> list = (List<MOrder>)((UISelectItems)child).getValue();
        				for (MOrder order: list) {
							if (order.getC_Order_ID() == Integer.parseInt(value))
								return order;
						}
        				
        			}
        		}  
        		return null;

            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid MOrder."));
            }
        }
        else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) throws ConverterException {
		
        if(object == null || (object instanceof String && ((String)object).trim().length() == 0)) {
        	return null;
        } else {	
            return String.valueOf(((MOrder)object).getC_Order_ID());
        }
	}


      
	           

}
