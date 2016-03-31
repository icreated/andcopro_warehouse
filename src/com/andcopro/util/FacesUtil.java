/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.util;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacesUtil
{
  public static Object getActionAttribute(ActionEvent event, String name)
  {
    return event.getComponent().getAttributes().get(name);
  }
  
  public static Object getSessionMapValue(String key)
  {
    return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
  }
  
  public static Object removeFromSession(String key)
  {
    return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(key);
  }
  
  public static void setSessionMapValue(String key, Object value)
  {
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
  }
  
  public static HttpServletRequest getRequest()
  {
    return (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }
  
  public static ServletContext getServletContext()
  {
    return (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
  }
  
  public static void sendRedirect(String url)
  {
    try
    {
      ((HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse()).sendRedirect(url);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static String lookupManagedBeanName(Object bean)
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    
    Map<String, Object> requestMap = externalContext.getRequestMap();
    for (String key : requestMap.keySet()) {
      if (bean.equals(requestMap.get(key))) {
        return key;
      }
    }
    Map<String, Object> sessionMap = externalContext.getSessionMap();
    for (String key : sessionMap.keySet()) {
      if (bean.equals(sessionMap.get(key))) {
        return key;
      }
    }
    Map<String,Object> applicationMap = externalContext.getApplicationMap();
    
    for (String key : (applicationMap).keySet()) {
      if (bean.equals(((Map)applicationMap).get(key))) {
        return key;
      }
    }
    return null;
  }
  
  
	public static void addSuccessMessage(String message) {
		addMessage(FacesMessage.SEVERITY_INFO, message, (String)null);	
	}
  
	public static void addErrorMessage(String message) {
		addMessage(FacesMessage.SEVERITY_ERROR, message, (String)null);	
	}
  
	public static void addWarnMessage(String message, String... params) {
		addMessage(FacesMessage.SEVERITY_WARN, message, params);	
	}
	public static void addWarnMessage(String message) {
		addMessage(FacesMessage.SEVERITY_WARN, message, (String)null);	
	}
	public static void addErrorMessage(String message, String... params) {
		addMessage(FacesMessage.SEVERITY_ERROR, message, params);	
	}
  
	public static void addSuccessMessage(String message, String... params) {
		addMessage(FacesMessage.SEVERITY_INFO, message, params);	
	}
	
	
	public static void addMessage(Severity severity, String message, String... params) {
			
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,"", String.format(message, params)));	
	}  
  
  
}
