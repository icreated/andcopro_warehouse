package com.andcopro.converter;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpServletRequest;

import org.compiere.util.KeyNamePair;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.selectonemenu.SelectOneMenu;

import com.andcopro.action.Stock;

@FacesConverter("knpConverter")
public class KeyNamePairConverter implements Converter {
	 


	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, final String value) throws ConverterException {
		
		System.out.println("Value= "+value);
        if(value != null && value.trim().length() > 0) {
            try {
            	
            	if (component instanceof AutoComplete) {
            		AutoComplete auto = (AutoComplete)component;
            		
            		FacesContext context = FacesContext.getCurrentInstance();
            		Stock stock = (Stock) context.getELContext().getELResolver().getValue(context.getELContext(), null, "stock");

            		KeyNamePair knp = context.getApplication().evaluateExpressionGet(context, "#{p}", KeyNamePair.class);
            		System.out.println("request= "+stock);
            		System.out.println(knp+"   aixo    "+value);
            		return knp;
            		
            	} else if (component instanceof SelectOneMenu) {
            	
	        		SelectOneMenu som = (SelectOneMenu)component;
	        		for(UIComponent child : som.getChildren()) {
	        			if (child instanceof UISelectItems) {
	        				@SuppressWarnings("unchecked")
	        				List<KeyNamePair> list = (List<KeyNamePair>)((UISelectItems)child).getValue();
	        				for (KeyNamePair knp: list) {
								if (knp.getKey() == Integer.parseInt(value))
									return knp;
							}
	        				
	        			}
	        		}  
            	}
        		return null;

            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid KeyNamePair."));
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
            return String.valueOf(((KeyNamePair)object).getKey());
        }
	}


      
	           

}
