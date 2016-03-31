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
import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesConverter("bpartnerConverter")
public class BpartnerConverter implements Converter {
	 

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, final String value) throws ConverterException {
		
        if(value != null && value.trim().length() > 0) {
            try {
            	
        		SelectOneMenu som = (SelectOneMenu)component;
        		for(UIComponent child : som.getChildren()) {
        			if (child instanceof UISelectItems) {
        				@SuppressWarnings("unchecked")
        				List<MBPartner> list = (List<MBPartner>)((UISelectItems)child).getValue();
        				for (MBPartner bpartner: list) {
							if (bpartner.getC_BPartner_ID() == Integer.parseInt(value))
								return bpartner;
						}
        				
        			}
        		}  
        		return null;

            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid BPartner."));
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
            return String.valueOf(((MBPartner)object).getC_BPartner_ID());
        }
	}


      
	           

}
