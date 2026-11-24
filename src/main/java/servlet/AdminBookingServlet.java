package com.gymbooking.servlet;

import com.gymbooking.dao.BookingDAO;
import com.gymbooking.exception.BookingException;
import com.gymbooking.model.Booking;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String slotIdParam = req.getParameter("slotId");
        List<Booking> bookings;
        if (slotIdParam != null && !slotIdParam.trim().isEmpty()) {
            try {
                int slotId = Integer.parseInt(slotIdParam.trim());
                bookings = bookingDAO.getBookingsBySlot(slotId);
                req.setAttribute("filterSlotId", slotId);
            } catch (NumberFormatException e) {
                bookings = bookingDAO.getAllBookings();
            }
        } else {
            bookings = bookingDAO.getAllBookings();
        }
        req.setAttribute("bookings", bookings);
        req.getRequestDispatcher("/admin/manage-bookings.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String slotIdParam = req.getParameter("slotId");
        String sep = "?";
        String redirectBase = req.getContextPath() + "/admin/bookings";
        if (slotIdParam != null && !slotIdParam.trim().isEmpty()) {
            redirectBase += "?slotId=" + slotIdParam.trim();
            sep = "&";
        }

        if ("cancel".equals(action)) {
            try {
                int bookingId = Integer.parseInt(req.getParameter("bookingId"));
                bookingDAO.cancelBooking(bookingId, 0, true);
                res.sendRedirect(redirectBase + sep + "success="
                    + URLEncoder.encode("Booking cancelled and slot availability restored.", StandardCharsets.UTF_8.name()));
            } catch (NumberFormatException e) {
                res.sendRedirect(redirectBase + sep + "error="
                    + URLEncoder.encode("Invalid booking ID.", StandardCharsets.UTF_8.name()));
            } catch (BookingException e) {
                res.sendRedirect(redirectBase + sep + "error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name()));
            }
        } else {
            res.sendRedirect(redirectBase);
        }
    }
}
