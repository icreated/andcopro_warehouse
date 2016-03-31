/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, 9 nov. 2015
 */
package com.andcopro.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.andcopro.service.OrderService;
import com.andcopro.service.ServiceFactory;

@ManagedBean
@RequestScoped
public class DocumentAttachment {
	
	CLogger log = CLogger.getCLogger(DocumentAttachment.class);
	
	
	
	int id;
	String type;
	
	OrderService orderService = (OrderService)ServiceFactory.get(OrderService.class);
	
    @PostConstruct
    public void init() {

    	
    	String strId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
    	try {
    		 id = Integer.parseInt(strId);
		} catch (NumberFormatException e) {}
    	
    	if (id <= 0)
    		return;    	
    	type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("type");
    	
    }    	
	
	
    

    public StreamedContent getStream() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        System.out.println("GET STREAMMMMMMMMMMMMMMMMMMMM");
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
        	
            String id = context.getExternalContext().getRequestParameterMap().get("aid");
            System.out.println("ID===== "+id);
            return getData(Integer.parseInt(id));
        }
    }    
    
    
    private DefaultStreamedContent getData(int id ) {
    	
    	System.out.println("INIT REPORT "+id);
    	OrderService orderService = (OrderService)ServiceFactory.get(OrderService.class);
    	File file = orderService.getOrder(id).createPDF();
    	System.out.println("ORDERRRRRRRRRRRRRRRRRRRRRRRR "+file);
		if (file == null)
			return null;
		
		byte[] data = null;
		
		try {
			FileInputStream fileInputStream=null;
	            //convert file into array of bytes
		    fileInputStream = new FileInputStream(file);
		    DefaultStreamedContent dsc = new DefaultStreamedContent(fileInputStream);
		    fileInputStream.close();
		    return dsc;
		    
		} catch(IOException e) {
			System.out.println("\nFailure : " + e.toString() + "\n");
		}   	
		return null;
    }
    
	
	
	
	public String download() {
		
		log.config("downloading... ");

		HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
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
			return null;

		response.setContentType("application/pdf");
                response.addHeader("Content-disposition", "attachment; filename=\"" + document.getDocumentNo() +"\"");
		try {
			FileInputStream fileInputStream=null;
	        byte[] bytes = new byte[(int) file.length()];
	            //convert file into array of bytes
		    fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bytes);
		    fileInputStream.close();
		       
		


			ServletOutputStream os = response.getOutputStream();
			os.write(bytes);
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch(IOException e) {
			System.out.println("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}
}