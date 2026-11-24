package com.gymbooking.servlet;

import com.gymbooking.dao.BookingDAO;
import com.gymbooking.exception.BookingException;
import com.gymbooking.model.User;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user/cancel-booking")
public class CancelBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        String bookingIdParam = req.getParameter("bookingId");

        try {
            int bookingId = Integer.parseInt(bookingIdParam);
            bookingDAO.cancelBooking(bookingId, currentUser.getUserId(), false);
            res.sendRedirect(req.getContextPath() + "/user/bookings?success="
                + URLEncoder.encode("Booking cancelled and slot availability restored.", StandardCharsets.UTF_8.name()));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/user/bookings?error="
                + URLEncoder.encode("Invalid booking ID.", StandardCharsets.UTF_8.name()));
        } catch (BookingException e) {
            res.sendRedirect(req.getContextPath() + "/user/bookings?error="
                + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name()));
        }
    }
}
