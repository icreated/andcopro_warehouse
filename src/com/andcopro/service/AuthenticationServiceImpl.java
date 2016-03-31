/* Copyright (C) Positiv Buildings - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Sergey Polyarus <polyarus@gmail.com>, October 2015
 */
package com.andcopro.service;

import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.compiere.model.MSession;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

import com.andcopro.bean.SessionUser;
import com.andcopro.bean.WUser;
import com.andcopro.util.CustomPasswordEncoder;
import com.andcopro.util.Envs;
import com.andcopro.util.FacesUtil;

public class AuthenticationServiceImpl
  implements AuthenticationService
{
  Properties ctx;
  HttpSession httpSession;
  CLogger log = CLogger.getCLogger(AuthenticationServiceImpl.class);
  
  public AuthenticationServiceImpl(HttpServletRequest request)
  {
    this.ctx = Envs.getCtx(request.getSession());
    this.httpSession = request.getSession();
  }
  
  public WUser getUserCredential()
  {
    return Envs.getUserCredential();
  }
  
  public boolean login(String login, String password)
  {
    WUser sessionUser = new SessionUser(this.ctx, login);
    if (sessionUser.getAD_User_ID() <= 0) {
      return false;
    }
    CustomPasswordEncoder encoder = new CustomPasswordEncoder(sessionUser.getSalt());
    CharSequence pass = password;
    boolean isValid = encoder.matches(pass, sessionUser.getPassword());
    if (!isValid) {
      return false;
    }
//    WUser cre = new SessionUser(this.ctx, sessionUser.getEmail());
    this.httpSession.setAttribute("userCredential", sessionUser);
    
    return true;
  }
  
  public void logout()
  {
    this.log.info("** logout");
    Env.getCtx().clear();
    FacesUtil.removeFromSession("userCredential");
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    MSession mSession = MSession.get(this.ctx, false);
    if (mSession != null) {
      mSession.logout();
    }
    FacesUtil.sendRedirect("index.xhtml");
  }
}
