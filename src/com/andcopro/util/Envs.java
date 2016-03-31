package com.andcopro.util;

import com.andcopro.bean.SessionUser;
import com.andcopro.bean.WUser;
import java.util.Enumeration;
import java.util.Properties;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.compiere.model.MClient;
import org.compiere.model.MRole;
import org.compiere.model.MStore;
import org.compiere.util.CCache;
import org.compiere.util.Env;

public class Envs
{
	
	public static final String ROLE_WAREHOUSE = "Warehouse";
	public static final String ROLE_SCHEDULING = "Scheduling";
	public static final String ROLE_PURCHASING = "Purchasing";
	public static final String ROLE_ACCOUNTING_RECEIVABLE = "Accounts Receivable";
	
	
	
  private static CCache<Integer, Properties> s_cacheCtx = new CCache(null, "EnvCtx", 120, 10, false);
  public static final String CTX_DOCUMENT_DIR = "documentDir";
  
  public static WUser getUserCredential()
  {
    WUser cre = (WUser)FacesUtil.getSessionMapValue("userCredential");
    if (cre == null)
    {
      cre = new SessionUser();
      FacesUtil.setSessionMapValue("userCredential", cre);
    }
    return cre;
  }
  
  public static WUser getUserCredential(HttpSession session)
  {
    WUser cre = (WUser)session.getAttribute("userCredential");
    if (cre == null)
    {
      cre = new SessionUser();
      session.setAttribute("userCredential", cre);
    }
    return cre;
  }
  
  public static MStore getWebStore()
  {
    return MStore.get(getCtx(), Env.getContextAsInt(getCtx(), "#W_Store_ID"));
  }
  
  public static Properties getCtx()
  {
    Properties ctx = (Properties)FacesUtil.getSessionMapValue("ctx");
    if (ctx != null) {
      return ctx;
    }
    ServletContext sctx = (ServletContext)
      FacesContext.getCurrentInstance().getExternalContext().getContext();
    
    ctx = getDefaults(sctx);
    FacesUtil.setSessionMapValue("ctx", ctx);
    
    return ctx;
  }
  
  public static Properties getCtx(HttpSession session)
  {
    Properties ctx = (Properties)session.getAttribute("ctx");
    if (ctx != null) {
      return ctx;
    }
    ServletContext sctx = session.getServletContext();
    ctx = getDefaults(sctx);
    session.setAttribute("ctx", ctx);
    System.out.println(ctx);
    System.out.println(MRole.getDefault(ctx, true));
    System.out.println("-------------------- "+MRole.getDefault());
    
    return ctx;
  }
  
  private static Properties getDefaults(ServletContext sc)
  {
    Properties ctx = new Properties();
    
    Enumeration<String> en = sc.getInitParameterNames();
    while (en.hasMoreElements())
    {
      String key = (String)en.nextElement();
      String value = sc.getInitParameter(key);
      ctx.setProperty(key, value);
    }
    MStore wstore = MStore.get(ctx, Env.getContextAsInt(ctx, "#W_Store_ID"));
    
    Integer key = new Integer(wstore.getW_Store_ID());
    Properties newCtx = (Properties)s_cacheCtx.get(key);
    if (newCtx == null)
    {
      newCtx = new Properties();
      
      Enumeration<?> e = ctx.keys();
      while (e.hasMoreElements())
      {
        String pKey = (String)e.nextElement();
        newCtx.setProperty(pKey, ctx.getProperty(pKey));
      }
      Env.setContext(newCtx, "#AD_Client_ID", wstore.getAD_Client_ID());
      Env.setContext(newCtx, "#AD_Org_ID", wstore.getAD_Org_ID());
      
      Env.setContext(newCtx, "#SalesRep_ID", wstore.getSalesRep_ID());
      Env.setContext(newCtx, "#M_PriceList_ID", wstore.getM_PriceList_ID());
      Env.setContext(newCtx, "#M_Warehouse_ID", wstore.getM_Warehouse_ID());
      
      String s = wstore.getWebParam1();
      Env.setContext(newCtx, "webParam1", s == null ? "" : s);
      s = wstore.getWebParam2();
      Env.setContext(newCtx, "webParam2", s == null ? "" : s);
      s = wstore.getWebParam3();
      Env.setContext(newCtx, "webParam3", s == null ? "" : s);
      s = wstore.getWebParam4();
      Env.setContext(newCtx, "webParam4", s == null ? "" : s);
      s = wstore.getWebParam5();
      Env.setContext(newCtx, "webParam5", s == null ? "" : s);
      s = wstore.getWebParam6();
      Env.setContext(newCtx, "webParam6", s == null ? "" : s);
      s = wstore.getStylesheet();
      if (s == null)
      {
        s = "standard";
      }
      else
      {
        int index = s.lastIndexOf('.');
        if (index != -1) {
          s = s.substring(0, index);
        }
      }
      Env.setContext(newCtx, "Stylesheet", s);
      
      Env.setContext(newCtx, "#M_PriceList_ID", wstore.getM_PriceList_ID());
      if (Env.getContextAsInt(newCtx, "#AD_User_ID") == 0) {
        Env.setContext(newCtx, "#AD_User_ID", wstore.getSalesRep_ID());
      }
      if (Env.getContextAsInt(newCtx, "#AD_Role_ID") == 0)
      {
        int AD_Role_ID = 0;
        Env.setContext(newCtx, "#AD_Role_ID", AD_Role_ID);
      }
      MClient client = MClient.get(newCtx, wstore.getAD_Client_ID());
      
      Env.setContext(newCtx, "name", client.getName());
      Env.setContext(newCtx, "description", client.getDescription());
      if ((newCtx.getProperty("#AD_Language") == null) && (client.getAD_Language() != null)) {
        Env.setContext(newCtx, "#AD_Language", client.getAD_Language());
      }
      String docDir = client.getDocumentDir();
      Env.setContext(newCtx, "documentDir", docDir == null ? "" : docDir);
      if (newCtx.getProperty("#AD_Language") == null) {
        Env.setContext(newCtx, "#AD_Language", "en_US");
      }
      s_cacheCtx.put(key, newCtx);
    }
    Enumeration<?> e = newCtx.keys();
    while (e.hasMoreElements())
    {
      String pKey = (String)e.nextElement();
      ctx.setProperty(pKey, newCtx.getProperty(pKey));
    }
    return ctx;
  }
}
