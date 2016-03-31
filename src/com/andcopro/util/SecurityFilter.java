package com.andcopro.util;

import com.andcopro.bean.WUser;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityFilter
  implements Filter
{
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest req = (HttpServletRequest)request;
    WUser webUser = (WUser)req.getSession().getAttribute("userCredential");
    String path = req.getRequestURI().substring(req.getContextPath().length());
 //   System.out.println(webUser+"             "+path);
    if (!path.contains("/index"))
    {
      if (webUser != null)
      {
        if ((webUser.getUsername() != null) && (!webUser.getUsername().equals("")))
        {
          chain.doFilter(request, response);
        }
        else
        {
          HttpServletResponse res = (HttpServletResponse)response;
          res.sendRedirect("index.jsp");
        }
      }
      else
      {
        HttpServletResponse res = (HttpServletResponse)response;
        res.sendRedirect("index.jsp");
      }
    }
    else {
      chain.doFilter(request, response);
    }
  }
  
  public void destroy() {}
  
  public void init(FilterConfig arg0)
    throws ServletException
  {}
}
