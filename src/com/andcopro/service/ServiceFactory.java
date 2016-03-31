/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.compiere.model.MStore;
import org.compiere.util.Env;

import com.andcopro.bean.WUser;
import com.andcopro.util.Envs;

public class ServiceFactory {
	

	
    public static <T extends AService> T get(HttpServletRequest request, Class<T> type) {
    	
    	HttpSession session = request.getSession();
    	Properties ctx = Envs.getCtx(session);
    	WUser webUser = Envs.getUserCredential(session);
    	MStore webStore = MStore.get(ctx, Env.getContextAsInt(ctx, "#W_Store_ID"));
    	
    	return get(ctx, webUser, webStore, type);
    	
    }
	
	
	
    public static <T extends AService> T get(Class<T> type) {
    	
    	Properties ctx = Envs.getCtx();
    	WUser webUser = Envs.getUserCredential();
    	MStore webStore = Envs.getWebStore();
    	
    	return get(ctx, webUser, webStore, type);
    	
    }

    private static <T extends AService> T get(Properties ctx, WUser webUser, MStore webStore, Class<T> type) {
//    	

    	Class[] cArg = new Class[3];
    	cArg[0] = Properties.class;
    	cArg[1] = WUser.class;
    	cArg[2] = MStore.class; 
	
     try {
		return (T)type.getDeclaredConstructor(cArg).newInstance(ctx, webUser, webStore);
		
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     return null;
}

}
