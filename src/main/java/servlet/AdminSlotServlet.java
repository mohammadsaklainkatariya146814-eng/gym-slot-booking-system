package com.gymbooking.servlet;

import com.gymbooking.dao.GymSlotDAO;
import com.gymbooking.exception.BookingException;
import com.gymbooking.model.GymSlot;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/slots")
public class AdminSlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GymSlotDAO slotDAO = new GymSlotDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String editId = req.getParameter("editId");
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                GymSlot editSlot = slotDAO.getSlotById(Integer.parseInt(editId.trim()));
                req.setAttribute("editSlot", editSlot);
            } catch (NumberFormatException ignored) {
            }
        }
        List<GymSlot> slots = slotDAO.getAllSlots();
        req.setAttribute("slots", slots);
        req.getRequestDispatcher("/admin/manage-slots.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String redirectBase = req.getContextPath() + "/admin/slots";

        try {
            if ("add".equals(action)) {
                Date slotDate = Date.valueOf(req.getParameter("slotDate"));
                Time startTime = parseTime(req.getParameter("startTime"));
                Time endTime = parseTime(req.getParameter("endTime"));
                int capacity = Integer.parseInt(req.getParameter("capacity"));
                slotDAO.addSlot(slotDate, startTime, endTime, capacity);
                res.sendRedirect(redirectBase + "?success=" + encode("Gym slot added successfully."));
            } else if ("update".equals(action)) {
                int slotId = Integer.parseInt(req.getParameter("slotId"));
                Date slotDate = Date.valueOf(req.getParameter("slotDate"));
                Time startTime = parseTime(req.getParameter("startTime"));
                Time endTime = parseTime(req.getParameter("endTime"));
                int capacity = Integer.parseInt(req.getParameter("capacity"));
                slotDAO.updateSlot(slotId, slotDate, startTime, endTime, capacity);
                res.sendRedirect(redirectBase + "?success=" + encode("Gym slot updated successfully."));
            } else if ("delete".equals(action)) {
                int slotId = Integer.parseInt(req.getParameter("slotId"));
                slotDAO.deleteSlot(slotId);
                res.sendRedirect(redirectBase + "?success=" + encode("Gym slot deleted."));
            } else {
                res.sendRedirect(redirectBase + "?error=" + encode("Unsupported action."));
            }
        } catch (IllegalArgumentException e) {
            res.sendRedirect(redirectBase + "?error=" + encode("Please enter valid date, time, and capacity values."));
        } catch (BookingException e) {
            res.sendRedirect(redirectBase + "?error=" + encode(e.getMessage()));
        }
    }

    private Time parseTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Time is required.");
        }
        String trimmed = raw.trim();
        if (trimmed.length() == 5) {
            trimmed = trimmed + ":00";
        }
        return Time.valueOf(trimmed);
    }

    private String encode(String msg) throws IOException {
        return URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
    }
}
