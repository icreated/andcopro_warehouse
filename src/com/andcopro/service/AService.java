/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.util.Properties;

import org.compiere.model.MStore;

import com.andcopro.bean.SessionUser;
import com.andcopro.bean.WUser;

public abstract class AService  {
	
	
	
	Properties ctx = null;
	
	MStore webStore = null;
	
	WUser sessionUser = null;
	
	AService(Properties ctx, 	WUser sessionUser, MStore webStore) {
		
		this.ctx = ctx;
		this.webStore = webStore;
		this.sessionUser = sessionUser;
	}
	
}