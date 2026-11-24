package com.gymbooking.dao;

import com.gymbooking.exception.BookingException;
import com.gymbooking.exception.DatabaseException;
import com.gymbooking.model.GymSlot;
import com.gymbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class GymSlotDAO {

    public List<GymSlot> getAllSlots() {
        List<GymSlot> slots = new ArrayList<>();
        String sql = "SELECT Slot_ID, Slot_Date, Start_Time, End_Time, Capacity, Available_Slots "
                   + "FROM GYM_SLOT ORDER BY Slot_Date ASC, Start_Time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                slots.add(mapSlot(rs));
            }
            return slots;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load gym slots.", e);
        }
    }

    public List<GymSlot> getUpcomingSlots(Date filterDate) {
        List<GymSlot> slots = new ArrayList<>();
        String sql = filterDate != null
            ? "SELECT Slot_ID, Slot_Date, Start_Time, End_Time, Capacity, Available_Slots "
              + "FROM GYM_SLOT WHERE Slot_Date = ? ORDER BY Start_Time ASC"
            : "SELECT Slot_ID, Slot_Date, Start_Time, End_Time, Capacity, Available_Slots "
              + "FROM GYM_SLOT WHERE Slot_Date >= CURDATE() ORDER BY Slot_Date ASC, Start_Time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filterDate != null) {
                ps.setDate(1, filterDate);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapSlot(rs));
                }
            }
            return slots;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch available slots.", e);
        }
    }

    public GymSlot getSlotById(int slotId) {
        String sql = "SELECT Slot_ID, Slot_Date, Start_Time, End_Time, Capacity, Available_Slots "
                   + "FROM GYM_SLOT WHERE Slot_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSlot(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch gym slot.", e);
        }
    }

    public void addSlot(Date slotDate, Time startTime, Time endTime, int capacity) throws BookingException {
        if (!endTime.after(startTime)) {
            throw new BookingException("End time must be after start time.");
        }
        if (capacity <= 0) {
            throw new BookingException("Capacity must be at least 1.");
        }
        String sql = "INSERT INTO GYM_SLOT (Slot_Date, Start_Time, End_Time, Capacity, Available_Slots) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, slotDate);
            ps.setTime(2, startTime);
            ps.setTime(3, endTime);
            ps.setInt(4, capacity);
            ps.setInt(5, capacity);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new BookingException("A gym slot with the same date and time already exists.");
            }
            throw new DatabaseException("Failed to add gym slot.", e);
        }
    }

    public void updateSlot(int slotId, Date slotDate, Time startTime, Time endTime, int newCapacity)
            throws BookingException {
        if (!endTime.after(startTime)) {
            throw new BookingException("End time must be after start time.");
        }
        if (newCapacity <= 0) {
            throw new BookingException("Capacity must be at least 1.");
        }

        String lockSql = "SELECT Capacity, Available_Slots FROM GYM_SLOT WHERE Slot_ID = ? FOR UPDATE";
        String updateSql = "UPDATE GYM_SLOT SET Slot_Date = ?, Start_Time = ?, End_Time = ?, "
                         + "Capacity = ?, Available_Slots = ? WHERE Slot_ID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement lockPs = conn.prepareStatement(lockSql)) {
                lockPs.setInt(1, slotId);
                try (ResultSet rs = lockPs.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new BookingException("Selected gym slot was not found.");
                    }
                    int oldCapacity = rs.getInt("Capacity");
                    int oldAvailable = rs.getInt("Available_Slots");
                    int bookedCount = oldCapacity - oldAvailable;

                    if (newCapacity < bookedCount) {
                        conn.rollback();
                        throw new BookingException(
                            "Capacity cannot be less than active bookings (" + bookedCount + ").");
                    }

                    int newAvailable = newCapacity - bookedCount;
                    try (PreparedStatement upPs = conn.prepareStatement(updateSql)) {
                        upPs.setDate(1, slotDate);
                        upPs.setTime(2, startTime);
                        upPs.setTime(3, endTime);
                        upPs.setInt(4, newCapacity);
                        upPs.setInt(5, newAvailable);
                        upPs.setInt(6, slotId);
                        upPs.executeUpdate();
                    }
                }
                conn.commit();
            } catch (BookingException e) {
                throw e;
            } catch (SQLException e) {
                conn.rollback();
                if (e.getErrorCode() == 1062) {
                    throw new BookingException("Another slot already exists for that date and time.");
                }
                throw new DatabaseException("Failed to update gym slot.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error while updating slot.", e);
        }
    }

    public boolean deleteSlot(int slotId) {
        String delBookingsSql = "DELETE FROM BOOKING WHERE Slot_ID = ?";
        String delSlotSql = "DELETE FROM GYM_SLOT WHERE Slot_ID = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psBookings = conn.prepareStatement(delBookingsSql)) {
                    psBookings.setInt(1, slotId);
                    psBookings.executeUpdate();
                }
                boolean deleted;
                try (PreparedStatement psSlot = conn.prepareStatement(delSlotSql)) {
                    psSlot.setInt(1, slotId);
                    deleted = psSlot.executeUpdate() > 0;
                }
                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to delete gym slot.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error while deleting slot.", e);
        }
    }

    public int countSlots() {
        String sql = "SELECT COUNT(*) FROM GYM_SLOT";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count gym slots.", e);
        }
    }

    private GymSlot mapSlot(ResultSet rs) throws SQLException {
        return new GymSlot(
            rs.getInt("Slot_ID"),
            rs.getDate("Slot_Date"),
            rs.getTime("Start_Time"),
            rs.getTime("End_Time"),
            rs.getInt("Capacity"),
            rs.getInt("Available_Slots")
        );
    }
}
