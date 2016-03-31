/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 9 nov. 2015
 */
package com.andcopro.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.compiere.process.DocAction;
import org.compiere.util.WebUtil;

import com.andcopro.bean.WUser;
import com.andcopro.service.OrderService;
import com.andcopro.service.ServiceFactory;
import com.andcopro.util.Envs;


public class DocumentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.    
    
	OrderService orderService;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	
		WUser webUser = Envs.getUserCredential(request.getSession());
		if (webUser.getAD_User_ID() <= 0 )
			return;
    	
    	
        orderService = (OrderService)ServiceFactory.get(request, OrderService.class);
        
        int id = WebUtil.getParameterAsInt(request, "id");
        String type = WebUtil.getParameter(request, "type");

        if (id == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        
		DocAction  document = null;
		if (type.equals("order")) {
			document = orderService.getOrder(id);
		} else if (type.equals("invoice")) {
			document = orderService.getInvoice(id);
		} else if (type.equals("shipment")) {
			document = orderService.getShipment(id);
		} else
			document = orderService.getOrder(id);
		
		
		
		
		
		File file = document.createPDF();
		
		if (file == null)
			return;
		
		
		FileInputStream fileInputStream=null;
        byte[] bytes = new byte[(int) file.length()];
            //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(bytes);
	    fileInputStream.close();
	      
        // Init servlet response.
        response.reset();
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setContentLength((int)file.length());
        response.getOutputStream().write(bytes);


    	
    }
    
    
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	doPost(request, response);
    }
    	


    
	

}
