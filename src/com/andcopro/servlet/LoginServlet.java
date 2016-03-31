/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.servlet;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.compiere.db.CConnection;
import org.compiere.model.MSession;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.WebUtil;

import com.andcopro.bean.WUser;
import com.andcopro.service.AuthenticationService;
import com.andcopro.service.AuthenticationServiceImpl;
import com.andcopro.util.Envs;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }
    
    
    private static CLogger logger;
	
	@Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

/*
        String propertyFile = Ini.getFileName(false);
        File file = new File(propertyFile);
        if (!file.exists())
        {
        	throw new IllegalStateException("idempiere.properties is not setup. PropertyFile="+propertyFile);
        }
        if (!Adempiere.isStarted())
        {
	        boolean started = Adempiere.startup(false);
	        if(!started)
	        {
	            throw new ServletException("Could not start iDempiere");
	        }
        }
*/
    
        Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i ++)
		{
			if (handlers[i] instanceof ConsoleHandler) {
				handlers[i].setLevel(Level.CONFIG);
			    }
		}		
		        

    }    

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	    throws ServletException, IOException
    {
    	    String username = WebUtil.getParameter(request, "username");
    	    String password = WebUtil.getParameter(request, "password");
    	    
    	    AuthenticationService authService = new AuthenticationServiceImpl(request);
    	    if (!authService.login(username, password))
    	    {
    	      String message = "OOps!!! Invalid Username/Password";
    	      request.setAttribute("message", message);
    	      response.sendRedirect(request.getContextPath() + "/index.jsp");
    	    }
    	    else
    	    {
    	    	HttpSession session = request.getSession();
    	    	WUser user = Envs.getUserCredential(session);
    	    	Properties ctx = Envs.getCtx(session);
    	    	Env.setContext(ctx, "#AD_User_ID", user.getAD_User_ID());
//    	    	System.out.println("###############" +user);
/*    	    	
    			MSession msession = MSession.get (ctx, true);		//	Start Session
    			msession.setWebSession(UUID.randomUUID().toString());
    			msession.setDescription(msession.getDescription() + " " + "Andco application");
    			msession.save();
    			*/
//    			CConnection.get().setAppServerCredential("AD_Session_ID#"+msession.getAD_Session_ID(), msession.getWebSession().toCharArray());

    	    	
    	    	response.sendRedirect(request.getContextPath() + "/index.xhtml");
    	    }
    }
    
    	  
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    	    throws ServletException, IOException
    {
    	    Properties ctx = (Properties)request.getSession().getAttribute("ctx");
    	    if (ctx != null) {
    	      MSession mSession = MSession.get(ctx, false);
    	      if (mSession != null) {
    	        mSession.logout();
    	      }
    	      ctx.clear();
    	    }
    	    request.getSession().removeAttribute("userCredential");
    	    request.getSession().invalidate();
    	    response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}