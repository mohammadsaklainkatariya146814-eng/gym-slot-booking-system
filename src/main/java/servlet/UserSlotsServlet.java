package com.gymbooking.servlet;

import com.gymbooking.dao.BookingDAO;
import com.gymbooking.dao.GymSlotDAO;
import com.gymbooking.model.GymSlot;
import com.gymbooking.model.User;
import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/user/slots")
public class UserSlotsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GymSlotDAO slotDAO = new GymSlotDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String dateParam = req.getParameter("date");
        Date filterDate = null;
        if (dateParam != null && !dateParam.trim().isEmpty()) {
            try {
                filterDate = Date.valueOf(dateParam.trim());
                req.setAttribute("selectedDate", dateParam.trim());
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", "Invalid date format selected.");
            }
        }

        List<GymSlot> slots = slotDAO.getUpcomingSlots(filterDate);
        req.setAttribute("slots", slots);

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        Set<Integer> bookedSlotIds = (currentUser != null)
            ? bookingDAO.getBookedSlotIdsByUser(currentUser.getUserId())
            : Collections.emptySet();
        req.setAttribute("bookedSlotIds", bookedSlotIds);

        req.getRequestDispatcher("/user/slots.jsp").forward(req, res);
    }
}
