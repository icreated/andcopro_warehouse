/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.comparator;


	import java.io.Serializable;
import java.util.Comparator;

import com.andcopro.bean.StockBean;
	 
	 
	public class StockBeanComparator implements Comparator<Object>, Serializable {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = -2127053833562854322L;
	     
	    private boolean asc = true;
	    private int type = 0;
	 
	    public StockBeanComparator(boolean asc, int type) {
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
	    	StockBean order1 = (StockBean) o1;
	    	StockBean order2 = (StockBean) o2;
	        String str1 = "";
	        String str2 = "";
	        
	        switch (type) {
	        case 1: // 
	            return order1.getLocator().compareTo(order2.getLocator()) * (asc ? 1 : -1);
	        case 2: 
	        	if (order1.getCostcoNo() != null)
	        		str1 = order1.getCostcoNo();
	        	if (order2.getCostcoNo() != null)
	        		str2 = order2.getCostcoNo();	        	
	            return str1.compareTo(str2) * (asc ? 1 : -1);
	            
	        case 3: // 
	            return order1.getPoDocumentNo().compareTo(order2.getPoDocumentNo()) * (asc ? 1 : -1);
	            
	        case 4: // 
	            return order1.getBpName().compareTo(order2.getBpName()) * (asc ? 1 : -1);	
	             	            
	            
	        default:
	            return order1.getPoDocumentNo().compareTo(order2.getPoDocumentNo()) * (asc ? 1 : -1);
	        }
	 
	    }	
	
	
	
}
