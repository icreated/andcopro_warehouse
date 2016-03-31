/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 6 nov. 2015
 */
package com.andcopro.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.compiere.model.MUser;

import com.andcopro.util.Envs;

@FacesConverter("userConverter")
public class UserConverter implements Converter {
	 

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, final String value) throws ConverterException {
		
        if(value != null && value.trim().length() > 0) {
            try {
            	
            	return MUser.get(Envs.getCtx(), Integer.parseInt(value) );

            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid User."));
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
            return String.valueOf(((MUser)object).getAD_User_ID());
        }
	}


      
	           

}
