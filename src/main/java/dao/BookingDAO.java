package com.gymbooking.dao;

import com.gymbooking.exception.BookingException;
import com.gymbooking.exception.DatabaseException;
import com.gymbooking.model.Booking;
import com.gymbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingDAO {

    /**
     * Books a gym slot inside an atomic transaction with row-level locking
     * (SELECT ... FOR UPDATE) to strictly prevent overbooking.
     */
    public void bookSlot(int userId, int slotId) throws BookingException {
        String duplicateSql = "SELECT 1 FROM BOOKING WHERE User_ID = ? AND Slot_ID = ? AND Status = 'CONFIRMED'";
        String lockSlotSql = "SELECT Available_Slots FROM GYM_SLOT WHERE Slot_ID = ? FOR UPDATE";
        String decrementSql = "UPDATE GYM_SLOT SET Available_Slots = Available_Slots - 1 WHERE Slot_ID = ?";
        String insertBookingSql = "INSERT INTO BOOKING (User_ID, Slot_ID, Status) VALUES (?, ?, 'CONFIRMED')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Prevent duplicate active booking for the same slot
                try (PreparedStatement dupPs = conn.prepareStatement(duplicateSql)) {
                    dupPs.setInt(1, userId);
                    dupPs.setInt(2, slotId);
                    try (ResultSet rs = dupPs.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            throw new BookingException("You have already booked this gym slot.");
                        }
                    }
                }

                // 2. Lock slot row and verify availability
                try (PreparedStatement lockPs = conn.prepareStatement(lockSlotSql)) {
                    lockPs.setInt(1, slotId);
                    try (ResultSet rs = lockPs.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new BookingException("Selected gym slot does not exist.");
                        }
                        int available = rs.getInt("Available_Slots");
                        if (available <= 0) {
                            conn.rollback();
                            throw new BookingException("This gym slot is already full.");
                        }
                    }
                }

                // 3. Decrement available slots and create booking
                try (PreparedStatement decPs = conn.prepareStatement(decrementSql);
                     PreparedStatement insPs = conn.prepareStatement(insertBookingSql)) {
                    decPs.setInt(1, slotId);
                    decPs.executeUpdate();

                    insPs.setInt(1, userId);
                    insPs.setInt(2, slotId);
                    insPs.executeUpdate();
                }

                conn.commit();
            } catch (BookingException e) {
                throw e;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to complete slot booking.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error during booking.", e);
        }
    }

    /**
     * Cancels a confirmed booking and restores the slot's Available_Slots count.
     * If userId > 0, enforces that the booking belongs to that user.
     */
    public void cancelBooking(int bookingId, int userId, boolean isAdmin) throws BookingException {
        String selectSql = isAdmin
            ? "SELECT Slot_ID, Status FROM BOOKING WHERE Booking_ID = ? FOR UPDATE"
            : "SELECT Slot_ID, Status FROM BOOKING WHERE Booking_ID = ? AND User_ID = ? FOR UPDATE";
        String updateBookingSql = "UPDATE BOOKING SET Status = 'CANCELLED' WHERE Booking_ID = ?";
        String restoreSlotSql = "UPDATE GYM_SLOT SET Available_Slots = LEAST(Capacity, Available_Slots + 1) WHERE Slot_ID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int slotId;
                try (PreparedStatement selPs = conn.prepareStatement(selectSql)) {
                    selPs.setInt(1, bookingId);
                    if (!isAdmin) {
                        selPs.setInt(2, userId);
                    }
                    try (ResultSet rs = selPs.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new BookingException("Booking record not found.");
                        }
                        if (!"CONFIRMED".equalsIgnoreCase(rs.getString("Status"))) {
                            conn.rollback();
                            throw new BookingException("This booking is already cancelled.");
                        }
                        slotId = rs.getInt("Slot_ID");
                    }
                }

                try (PreparedStatement upBooking = conn.prepareStatement(updateBookingSql);
                     PreparedStatement upSlot = conn.prepareStatement(restoreSlotSql)) {
                    upBooking.setInt(1, bookingId);
                    upBooking.executeUpdate();

                    upSlot.setInt(1, slotId);
                    upSlot.executeUpdate();
                }

                conn.commit();
            } catch (BookingException e) {
                throw e;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to cancel booking.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error during cancellation.", e);
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.Booking_ID, b.User_ID, b.Slot_ID, b.Booking_Date, b.Status, "
                   + "s.Slot_Date, s.Start_Time, s.End_Time "
                   + "FROM BOOKING b "
                   + "JOIN GYM_SLOT s ON b.Slot_ID = s.Slot_ID "
                   + "WHERE b.User_ID = ? "
                   + "ORDER BY b.Booking_Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("Booking_ID"));
                    b.setUserId(rs.getInt("User_ID"));
                    b.setSlotId(rs.getInt("Slot_ID"));
                    b.setBookingDate(rs.getTimestamp("Booking_Date"));
                    b.setStatus(rs.getString("Status"));
                    b.setSlotDate(rs.getDate("Slot_Date"));
                    b.setStartTime(rs.getTime("Start_Time"));
                    b.setEndTime(rs.getTime("End_Time"));
                    list.add(b);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user bookings.", e);
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.Booking_ID, b.User_ID, b.Slot_ID, b.Booking_Date, b.Status, "
                   + "u.Name AS User_Name, u.Email AS User_Email, u.Phone AS User_Phone, "
                   + "s.Slot_Date, s.Start_Time, s.End_Time "
                   + "FROM BOOKING b "
                   + "JOIN USER u ON b.User_ID = u.User_ID "
                   + "JOIN GYM_SLOT s ON b.Slot_ID = s.Slot_ID "
                   + "ORDER BY b.Booking_Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("Booking_ID"));
                b.setUserId(rs.getInt("User_ID"));
                b.setSlotId(rs.getInt("Slot_ID"));
                b.setBookingDate(rs.getTimestamp("Booking_Date"));
                b.setStatus(rs.getString("Status"));
                b.setUserName(rs.getString("User_Name"));
                b.setUserEmail(rs.getString("User_Email"));
                b.setUserPhone(rs.getString("User_Phone"));
                b.setSlotDate(rs.getDate("Slot_Date"));
                b.setStartTime(rs.getTime("Start_Time"));
                b.setEndTime(rs.getTime("End_Time"));
                list.add(b);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all bookings.", e);
        }
    }

    public int countBookingsByStatus(String status) {
        String sql = status == null
            ? "SELECT COUNT(*) FROM BOOKING"
            : "SELECT COUNT(*) FROM BOOKING WHERE Status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count bookings.", e);
        }
    }

    public Set<Integer> getBookedSlotIdsByUser(int userId) {
        Set<Integer> slotIds = new HashSet<>();
        String sql = "SELECT Slot_ID FROM BOOKING WHERE User_ID = ? AND Status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slotIds.add(rs.getInt("Slot_ID"));
                }
            }
            return slotIds;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user booked slot IDs.", e);
        }
    }

    public List<Booking> getBookingsBySlot(int slotId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.Booking_ID, b.User_ID, b.Slot_ID, b.Booking_Date, b.Status, "
                   + "u.Name AS User_Name, u.Email AS User_Email, u.Phone AS User_Phone, "
                   + "s.Slot_Date, s.Start_Time, s.End_Time "
                   + "FROM BOOKING b "
                   + "JOIN USER u ON b.User_ID = u.User_ID "
                   + "JOIN GYM_SLOT s ON b.Slot_ID = s.Slot_ID "
                   + "WHERE b.Slot_ID = ? "
                   + "ORDER BY b.Booking_Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("Booking_ID"));
                    b.setUserId(rs.getInt("User_ID"));
                    b.setSlotId(rs.getInt("Slot_ID"));
                    b.setBookingDate(rs.getTimestamp("Booking_Date"));
                    b.setStatus(rs.getString("Status"));
                    b.setUserName(rs.getString("User_Name"));
                    b.setUserEmail(rs.getString("User_Email"));
                    b.setUserPhone(rs.getString("User_Phone"));
                    b.setSlotDate(rs.getDate("Slot_Date"));
                    b.setStartTime(rs.getTime("Start_Time"));
                    b.setEndTime(rs.getTime("End_Time"));
                    list.add(b);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch bookings for slot " + slotId, e);
        }
    }
}
