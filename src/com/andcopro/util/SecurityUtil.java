package com.andcopro.util;

import com.andcopro.bean.WUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SecurityUtil
{
  public static WUser getUser()
  {
    WUser webUser = (WUser)FacesUtil.getSessionMapValue("userCredential");
    return webUser;
  }
  
  public static boolean isNotGranted(String authorities)
  {
    if ((authorities == null) || ("".equals(authorities))) {
      return false;
    }
    Collection<String> granted = getPrincipalAuthorities();
    
    Set grantedCopy = retainAll(granted, parseAuthorities(authorities));
    return grantedCopy.isEmpty();
  }
  
  public static boolean isAllGranted(String authorities)
  {
    if ((authorities == null) || ("".equals(authorities))) {
      return false;
    }
    Collection<String> granted = getPrincipalAuthorities();
    boolean isAllGranted = granted.containsAll(parseAuthorities(authorities));
    return isAllGranted;
  }
  
  public static boolean isAnyGranted(String authorities)
  {
    if ((authorities == null) || ("".equals(authorities))) {
      return false;
    }
    Collection<String> granted = getPrincipalAuthorities();
    Set grantedCopy = retainAll(granted, parseAuthorities(authorities));
    return !grantedCopy.isEmpty();
  }
  
  private static Collection<String> getPrincipalAuthorities()
  {
    WUser currentUser = getUser();
    if (currentUser == null) {
      return Collections.emptyList();
    }
    if ((currentUser.getAuthorities() == null) || (currentUser.getAuthorities().isEmpty())) {
      return Collections.emptyList();
    }
    Collection<String> granted = currentUser.getAuthorities();
    return granted;
  }
  
  private static Collection<String> parseAuthorities(String authorizationsString)
  {
    ArrayList<String> required = new ArrayList();
    String[] roles = authorizationsString.split(",");
    for (int i = 0; i < roles.length; i++)
    {
      String role = roles[i].trim();
      required.add(role);
    }
    return required;
  }
  
  private static Set retainAll(Collection<String> granted, Collection<String> required)
  {
    Set<String> grantedRoles = toRoles(granted);
    Set<String> requiredRoles = toRoles(required);
    grantedRoles.retainAll(requiredRoles);
    
    return toAuthorities(grantedRoles, granted);
  }
  
  private static Set<String> toRoles(Collection<String> authorities)
  {
    Set<String> target = new HashSet();
    for (String au : authorities)
    {
      if (au == null) {
        throw new IllegalArgumentException(
          "Cannot process GrantedAuthority objects which return null from getAuthority() - attempting to process " + 
          au.toString());
      }
      target.add(au);
    }
    return target;
  }
  
  private static Set<String> toAuthorities(Set<String> grantedRoles, Collection<String> granted)
  {
    Set<String> target = new HashSet();
    for (String role : grantedRoles) {
      for (String authority : granted) {
        if (authority.equals(role))
        {
          target.add(authority);
          break;
        }
      }
    }
    return target;
  }
}
