/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.compiere.util.CLogger;
import org.compiere.util.Secure;
import org.compiere.util.SecureEngine;

public class CustomPasswordEncoder  {
	
	private CLogger log = CLogger.getCLogger(CustomPasswordEncoder.class);
	

	String salt = "0000000000000000";
	
	
	public CustomPasswordEncoder(String salt) {
		
		
		this.salt = salt;
		
	}


	public String encodePassword(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	


	public boolean matches(CharSequence password, String hash) {
		
		boolean valid=false;
		String encoded = null;
		
		if ( hash == null )
			hash = "0000000000000000";

			try {
				encoded = SecureEngine.getSHA512Hash(1000, password.toString(), Secure.convertHexString(salt));
				valid= encoded.equals(hash);
			} catch (NoSuchAlgorithmException ignored) {
				log.warning("Password hashing not supported by JVM");
			} catch (UnsupportedEncodingException ignored) {
				log.warning("Password hashing not supported by JVM");
			}			

	
		return valid;
	}




}
