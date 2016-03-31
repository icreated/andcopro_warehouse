/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.servlet;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.compiere.model.MAssignmentSlot;
import org.compiere.model.MResource;
import org.compiere.model.MResourceAssignment;
import org.compiere.model.MResourceType;
import org.compiere.model.ScheduleUtil;
import org.compiere.util.CLogMgt;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

import com.andcopro.bean.DayEvent;
import com.andcopro.bean.JsonAction;
import com.andcopro.bean.SchedulerEvent;
import com.andcopro.util.SchedulerModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *  Schedule Servlet
 *  @author     Sergey Polyarus
 *  @version 1.0
 *  @since   2015-10-25
 */

public class SchedulerServlet extends HttpServlet {
	 
    private static final long serialVersionUID = 1L;
 
    Properties ctx;


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
 
        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";
        if(br != null){
            json = br.readLine();
        }
//        System.out.println("Calling: "+json);
        
        ctx = (Properties)request.getSession().getAttribute("ctx");
        JsonAction result = null;
        Object jsonOutput = null;
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

        try {
            result = gson.fromJson(json, JsonAction.class);
        } catch(com.google.gson.JsonSyntaxException ex) { 
        	System.out.println(ex);
            return;
        }

      
        if (result.getAction().equals("events")) {
        	
        	int resourceId = result.getResourceId();
            if (resourceId <= 0)
            	return;
            jsonOutput = getModel(resourceId);
        } else if (result.getAction().equals("updateEvent") || result.getAction().equals("createEvent")) {	
        	
        	jsonOutput = updateEvent(result.getResourceId(), result.getEvent());
        	
        } else if (result.getAction().equals("deleteEvent")) {
        	
        	deleteEvent(result.getEvent());
        	jsonOutput = result.getEvent();
        } else
        	return;
        
        
        sendResponse(response, jsonOutput);   

        
    }
    
    
    
    private SchedulerEvent updateEvent(int S_Resource_ID, SchedulerEvent event) {
    	    	
    	MResourceAssignment mas = new MResourceAssignment(ctx, event.getId(), null);
    	mas.setS_Resource_ID(S_Resource_ID);
    	
    	Calendar cal = Calendar.getInstance();

    	mas.setAssignDateFrom(new Timestamp(event.getStartDate().getTime()));
    	
    	if (event.getEndDate() == null) {
    		cal.setTime(event.getStartDate());
    		cal.add(Calendar.HOUR_OF_DAY, 2);    
        	event.setEndDate(cal.getTime());
    	} else
    		cal.setTime(event.getEndDate());
    		
    	mas.setAssignDateTo(new Timestamp(cal.getTime().getTime()));
    	mas.set_CustomColumn("C_Order_ID", event.getOrderId());
//System.out.println(event.get);
    	if (event.getId() <=0 ) {
    		mas.setName(event.getTitle());
    		mas.setDescription(event.getDescription());
    		event.setEditable(true);
    	}
    	mas.save();
    	event.setId(mas.getS_ResourceAssignment_ID());
    	
    	return event;
    }
    
    
    private void deleteEvent(SchedulerEvent event) {
    	event.setEditable(true);
    	int id = DB.executeUpdate("DELETE FROM S_ResourceAssignment WHERE S_ResourceAssignment_ID = ?", event.getId(), null);
    }    
    
    
    
	private SchedulerModel getModel(int selectedResourceId ) {
		
		
		ScheduleUtil m_model = new ScheduleUtil (ctx);
		
		//		Calculate Start Day
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date(System.currentTimeMillis()));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		Timestamp startDate = new Timestamp(cal.getTimeInMillis());
		//	Calculate End Date
//		cal.add(Calendar.MONTH, 1);
//		cal.add(Calendar.DAY_OF_MONTH, 14);
		cal.setTime(new Timestamp(System.currentTimeMillis()));
		cal.add(Calendar.DAY_OF_MONTH, 60);
		Timestamp endDate = new Timestamp (cal.getTimeInMillis());		
		
	    SchedulerModel eventModel = new SchedulerModel();
	    SchedulerEvent previousEvent = null;
		
		if (selectedResourceId > 0) {
			MAssignmentSlot[] list = m_model.getAssignmentSlots (selectedResourceId, startDate, endDate, null, true, null);
			
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
			Map<String, DayEvent> workDays = new HashMap<String, DayEvent>();
			String key = null;
			int difference = 0;
			DayEvent devent = null;
			
			
			MResourceType resourceType = MResourceType.get(ctx, MResource.get(ctx, selectedResourceId).getS_ResourceType_ID());
			
			long dayDuration = resourceType.getDayDurationMillis();
			
			for (MAssignmentSlot mas: list) {
				if (mas.getMAssignment() == null)
					continue;
				
				devent = new DayEvent();
				
				
				key = dt.format(mas.getMAssignment().getAssignDateFrom());
				if (workDays.containsKey(key)) {
					devent.setWorkedTime((int)TimeUnit.MILLISECONDS.toMinutes(mas.getMAssignment().getAssignDateTo().getTime() 
							- mas.getMAssignment().getAssignDateFrom().getTime())+workDays.get(key).getWorkedTime()); 
					devent.setDayEvent(workDays.get(key).getDayEvent()+1);
					devent.setFreeTime((int)TimeUnit.MILLISECONDS.toMinutes((int)dayDuration)-devent.getWorkedTime());
				} else {
					devent.setWorkedTime((int)TimeUnit.MILLISECONDS.toMinutes(mas.getMAssignment().getAssignDateTo().getTime() 
							- mas.getMAssignment().getAssignDateFrom().getTime()));
					devent.setDayEvent(1);
					devent.setFreeTime((int)TimeUnit.MILLISECONDS.toMinutes((int)dayDuration)-devent.getWorkedTime());
				}
				workDays.put(key, devent);

			}
			
			
			

			
			for(MAssignmentSlot mas : list) {
				
				Timestamp startTime = mas.getStartTime();
				Timestamp endTime = mas.getEndTime();
				Calendar calStart = Calendar.getInstance();
				calStart.setTime(startTime);
				Calendar calEnd = Calendar.getInstance();
				calEnd.setTime(endTime);
				
				calStart.add(Calendar.DAY_OF_MONTH, 1);
				if (calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR) && calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)
					&& calStart.get(Calendar.DAY_OF_MONTH) == calEnd.get(Calendar.DAY_OF_MONTH)) {
					if (calEnd.get(Calendar.HOUR_OF_DAY) == 0 && calEnd.get(Calendar.MINUTE) == 0) {
						calEnd.add(Calendar.DAY_OF_MONTH, -1);
						calEnd.set(Calendar.HOUR_OF_DAY, 23);
					}
				}
				if (mas.getMAssignment() == null)
					continue;
				
			
				SchedulerEvent event = new SchedulerEvent(mas.getMAssignment().getS_ResourceAssignment_ID(), mas.getMAssignment().get_ValueAsInt("C_Order_ID"),
						mas.getName(), new Date(startTime.getTime()),calEnd.getTime());
				event.setDescription(mas.getDescription() != null ? mas.getDescription() : mas.getName());
				
				Color color = mas.getColor(true);	
				String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
				event.setBackgroundColor(hexColor);
				event.setStyleClass("scheduleEvent"+mas.getStatus());
			
//				if (!mas.isAssignment() || mas.getMAssignment().isConfirmed())
//					event.setEditable(false);
				
				DayEvent day = workDays.get(dt.format(mas.getMAssignment().getAssignDateFrom()));
				event.setWorkedTime(day.getWorkedTime());
				event.setDayEvent(day.getDayEvent());
				event.setFreeTime(day.getFreeTime());
				event.setAddress(getAddress(mas.getMAssignment().get_ValueAsInt("C_Order_ID")));

				eventModel.addEvent(event);

			}

		}

		
		return eventModel;
	}

	public Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}	

  
	private void sendResponse(HttpServletResponse response, Object jsonOutput) {
		
		Gson gson = new Gson();
        String str = gson.toJson(jsonOutput);
        
        PrintWriter out;
		try {
			out = response.getWriter();
	        response.setContentType("application/json");  
	        response.setHeader("Cache-control", "no-cache, no-store");
	        response.setHeader("Pragma", "no-cache");
	        response.setHeader("Expires", "-1");
	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST");
	        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	        response.setHeader("Access-Control-Max-Age", "86400");        
	        
	        out.write(str);
	        out.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        		
		
	}
    
	
	private String getAddress(int C_Order_ID) {
		
		if (C_Order_ID <= 0)
			return null;
		
		String sql = "SELECT l.Address1 || ', ' || l.City ||', ' || r.Name ||' ' ||  l.Postal FROM C_Order o " +
			"INNER JOIN C_BPartner_Location bp ON bp.C_BPartner_Location_ID = o.C_BPartner_Location_ID " +
			"INNER JOIN C_Location l ON bp.C_Location_ID = l.C_Location_ID " +
			"INNER JOIN C_Region r ON r.C_Region_ID = l.C_Region_ID " +
			"WHERE C_Order_ID = ?" ;		
		
		return DB.getSQLValueString(null, sql, C_Order_ID);
		}
    
    
}