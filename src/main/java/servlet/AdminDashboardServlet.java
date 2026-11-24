package com.gymbooking.servlet;

import com.gymbooking.dao.BookingDAO;
import com.gymbooking.dao.GymSlotDAO;
import com.gymbooking.dao.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final GymSlotDAO slotDAO = new GymSlotDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("totalMembers", userDAO.countMembers());
        req.setAttribute("totalSlots", slotDAO.countSlots());
        req.setAttribute("confirmedBookings", bookingDAO.countBookingsByStatus("CONFIRMED"));
        req.setAttribute("cancelledBookings", bookingDAO.countBookingsByStatus("CANCELLED"));
        req.setAttribute("recentBookings", bookingDAO.getAllBookings());
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, res);
    }
}
