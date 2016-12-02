/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.opentheso.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mom.trd.opentheso.SelectedBeans.CurrentUser;
import mom.trd.opentheso.SelectedBeans.SelectedCandidat;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;

public class loginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        CurrentUser session = (CurrentUser) req.getSession().getAttribute("user1");
        String url = req.getRequestURI();
        if(session != null) {
//            resp.sendRedirect("./install.xhtml");
            if(session.getConnect().getPoolConnexion() == null) {
                resp.sendRedirect("./install.xhtml");
                return;
            }
        }
        
        if(url.contains("deco.xhtml")) {
            CurrentUser temp = (CurrentUser)req.getSession().getAttribute("user1");
            
            temp.setUser(new NodeUser());
            temp.setIsLogged(false);
            req.getSession().setAttribute("user1", temp);
            
            ((SelectedCandidat)req.getSession().getAttribute("selectedCandidat")).reInit();
            
            resp.sendRedirect("./index.xhtml");//req.getServletContext().getContextPath());
        }
        else if(session == null || !session.isLogged()) {
            if(url.contains("conf.xhtml") || url.contains("gestCandidat.xhtml") || url.contains("edition.xhtml") || url.contains("statistic.xhtml")) {
                resp.sendRedirect(req.getServletContext().getContextPath());
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if(url.contains("connect.xhtml")) {
                resp.sendRedirect(req.getServletContext().getContextPath());
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        
    }
    
}
