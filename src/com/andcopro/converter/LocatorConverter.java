package com.andcopro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.andcopro.bean.Locator;

@FacesConverter("locatorConverter")
public class LocatorConverter extends SelectItemsBaseConverter {
	 


	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) throws ConverterException {


        if(object == null) {
        	return null;
        } else {
            return String.valueOf(((Locator)object).getId());
        }
        
	}


      
	           

}
