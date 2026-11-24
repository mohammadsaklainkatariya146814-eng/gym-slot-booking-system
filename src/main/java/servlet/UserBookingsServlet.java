package com.gymbooking.servlet;

import com.gymbooking.dao.BookingDAO;
import com.gymbooking.model.Booking;
import com.gymbooking.model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user/bookings")
public class UserBookingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        List<Booking> bookings = bookingDAO.getBookingsByUser(currentUser.getUserId());
        req.setAttribute("bookings", bookings);
        req.getRequestDispatcher("/user/bookings.jsp").forward(req, res);
    }
}
