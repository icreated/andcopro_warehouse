/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.comparator;


	import java.io.Serializable;
import java.util.Comparator;

import com.andcopro.bean.InstallBean;
	 
	 
	public class OrderComparator implements Comparator<Object>, Serializable {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = -2127053833562854322L;
	     
	    private boolean asc = true;
	    private int type = 0;
	 
	    public OrderComparator(boolean asc, int type) {
	        this.asc = asc;
	        this.type = type;
	    }
	 
	    public int getType() {
	        return type;
	    }
	 
	    public void setType(int type) {
	        this.type = type;
	    }
	 
	    @Override
	    public int compare(Object o1, Object o2) {
	    	InstallBean order1 = (InstallBean) o1;
	    	InstallBean order2 = (InstallBean) o2;
	        String str1 = "";
	        String str2 = "";
	        
	        switch (type) {
	        case 1: // 
	            return order1.getOrderDocumentNo().compareTo(order2.getOrderDocumentNo()) * (asc ? 1 : -1);
	        case 2: 
	        	return order1.getName().compareTo(order2.getName()) * (asc ? 1 : -1);	
	                
	        case 3: // 
	            return order1.getBpName().compareTo(order2.getBpName()) * (asc ? 1 : -1);	
	            
	        case 4: // 
	            return order1.getAddress1().compareTo(order2.getAddress1()) * (asc ? 1 : -1);	  
	            	             	            
	            
	        default:
	            return order1.getOrderDocumentNo().compareTo(order1.getOrderDocumentNo()) * (asc ? 1 : -1);
	        }
	 
	    }	
	
	
	
}
