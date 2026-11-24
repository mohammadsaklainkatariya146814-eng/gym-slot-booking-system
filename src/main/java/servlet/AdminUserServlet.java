package com.gymbooking.servlet;

import com.gymbooking.dao.UserDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("users", userDAO.getAllUsers());
        req.getRequestDispatcher("/admin/manage-users.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String redirectBase = req.getContextPath() + "/admin/users";
        if ("delete".equals(action)) {
            try {
                int userId = Integer.parseInt(req.getParameter("userId"));
                boolean deleted = userDAO.deleteUser(userId);
                String msg = deleted ? "User and all associated bookings removed successfully." : "Unable to delete this account.";
                res.sendRedirect(redirectBase + (deleted ? "?success=" : "?error=")
                    + URLEncoder.encode(msg, StandardCharsets.UTF_8.name()));
            } catch (NumberFormatException e) {
                res.sendRedirect(redirectBase + "?error="
                    + URLEncoder.encode("Invalid user ID.", StandardCharsets.UTF_8.name()));
            }
        } else {
            res.sendRedirect(redirectBase);
        }
    }
}
