package com.gymbooking.filter;

import com.gymbooking.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/user/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow public access to admin login endpoint
        if ("/admin/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            if (path.startsWith("/admin/")) {
                res.sendRedirect(req.getContextPath() + "/admin-login.jsp?error=Please+log+in+as+administrator.");
            } else {
                res.sendRedirect(req.getContextPath() + "/login.jsp?error=Please+log+in+to+continue.");
            }
            return;
        }

        if (path.startsWith("/admin/") && !currentUser.isAdmin()) {
            res.sendRedirect(req.getContextPath() + "/user/slots?error=Administrator+access+required.");
            return;
        }

        // Prevent caching of protected pages after logout
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
