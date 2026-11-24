package com.gymbooking.servlet;

import com.gymbooking.dao.UserDAO;
import com.gymbooking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || password == null || password.length() < 6) {
            req.setAttribute("error", "All fields are required and password must be at least 6 characters.");
            req.getRequestDispatcher("/register.jsp").forward(req, res);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/register.jsp").forward(req, res);
            return;
        }

        if (userDAO.emailExists(email)) {
            req.setAttribute("error", "An account with this email already exists.");
            req.getRequestDispatcher("/register.jsp").forward(req, res);
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);

        userDAO.registerUser(user);
        res.sendRedirect(req.getContextPath() + "/login.jsp?success=Account+created+successfully.+Please+log+in.");
    }
}
