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

@WebServlet("/user/book")
public class BookSlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        String slotIdParam = req.getParameter("slotId");

        try {
            int slotId = Integer.parseInt(slotIdParam);
            bookingDAO.bookSlot(currentUser.getUserId(), slotId);
            res.sendRedirect(req.getContextPath() + "/user/bookings?success="
                + URLEncoder.encode("Gym slot booked successfully!", StandardCharsets.UTF_8.name()));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/user/slots?error="
                + URLEncoder.encode("Invalid slot selected.", StandardCharsets.UTF_8.name()));
        } catch (BookingException e) {
            res.sendRedirect(req.getContextPath() + "/user/slots?error="
                + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name()));
        }
    }
}
