package com.gymbooking.servlet;

import com.gymbooking.dao.UserDAO;
import com.gymbooking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/admin-login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Please enter administrator email and password.");
            req.getRequestDispatcher("/admin-login.jsp").forward(req, res);
            return;
        }

        User admin = userDAO.authenticate(email, password, "ADMIN");
        if (admin == null) {
            req.setAttribute("error", "Invalid administrator credentials.");
            req.getRequestDispatcher("/admin-login.jsp").forward(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", admin);
        session.setMaxInactiveInterval(30 * 60);
        res.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
