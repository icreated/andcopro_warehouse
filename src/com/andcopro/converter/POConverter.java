package com.andcopro.converter;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.compiere.model.MOrder;
import org.compiere.model.MResource;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesConverter("poConverter")
public class POConverter implements Converter {
	 

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) throws ConverterException {
		
		
		System.out.println("VALUE="+value);
		System.out.println("COMP+"+component);
		System.out.println(component.getAttributes().get("po"));
		System.out.println(component.getAttributes().get("list"));
		
        if(value != null && value.trim().length() > 0) {
            try {
            	
        		SelectOneMenu som = (SelectOneMenu)component;
        		for(UIComponent child : som.getChildren()) {
        			if (child instanceof UISelectItems) {
        				@SuppressWarnings("unchecked")
        				List<MResource> list = (List<MResource>)((UISelectItems)child).getValue();
        				for (MResource order: list) {
							if (order.getS_Resource_ID() == Integer.parseInt(value))
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
            return String.valueOf(((MResource)object).getS_Resource_ID());
        }
	}



      
	           

}
