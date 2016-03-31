package com.andcopro.bean;

import java.util.Collection;

public interface WUser {

	String getUsername();

	Collection<String> getAuthorities();

	boolean isAccountNonExpired();

	boolean isAccountNonLocked();

	boolean isCredentialsNonExpired();

	boolean isEnabled();

	String getEmail();
	
	String getPassword();
	
	int getAD_User_ID();
	
	String getSalt();
	
	int getC_BP_Group_ID();
	
	int getC_BPartner_ID();
	
}