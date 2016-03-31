/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 13 nov. 2015
 */
package com.andcopro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.andcopro.util.SelectItemsUtils;

public abstract class SelectItemsBaseConverter implements Converter {
	
	
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {   
        return SelectItemsUtils.findValueByStringConversion(context, component, value, this);    
    }    
}