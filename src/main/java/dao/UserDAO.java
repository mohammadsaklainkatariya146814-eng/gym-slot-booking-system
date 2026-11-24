package com.gymbooking.dao;

import com.gymbooking.exception.DatabaseException;
import com.gymbooking.model.User;
import com.gymbooking.util.DBConnection;
import com.gymbooking.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM USER WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check existing email.", e);
        }
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO USER (Name, Email, Phone, Password, Role) VALUES (?, ?, ?, ?, 'USER')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName().trim());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPhone().trim());
            ps.setString(4, PasswordUtil.hashPassword(user.getPassword()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register user.", e);
        }
    }

    public User authenticate(String email, String plainPassword, String requiredRole) {
        String sql = "SELECT User_ID, Name, Email, Phone, Password, Role FROM USER WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("Password");
                    String role = rs.getString("Role");
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        if (requiredRole == null || requiredRole.equalsIgnoreCase(role)) {
                            return mapUser(rs);
                        }
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Authentication query failed.", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT User_ID, Name, Email, Phone, Password, Role FROM USER ORDER BY User_ID DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users.", e);
        }
    }

    public boolean deleteUser(int userId) {
        String checkAdminSql = "SELECT Role FROM USER WHERE User_ID = ?";
        String findSlotsSql = "SELECT Slot_ID FROM BOOKING WHERE User_ID = ? AND Status = 'CONFIRMED'";
        String restoreSlotSql = "UPDATE GYM_SLOT SET Available_Slots = LEAST(Capacity, Available_Slots + 1) WHERE Slot_ID = ?";
        String deleteBookingsSql = "DELETE FROM BOOKING WHERE User_ID = ?";
        String deleteUserSql = "DELETE FROM USER WHERE User_ID = ? AND Role <> 'ADMIN'";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Ensure user exists and is not an ADMIN
                try (PreparedStatement checkPs = conn.prepareStatement(checkAdminSql)) {
                    checkPs.setInt(1, userId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (!rs.next() || "ADMIN".equalsIgnoreCase(rs.getString("Role"))) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // 2. Find confirmed bookings to restore slot capacity
                List<Integer> slotsToRestore = new ArrayList<>();
                try (PreparedStatement findPs = conn.prepareStatement(findSlotsSql)) {
                    findPs.setInt(1, userId);
                    try (ResultSet rs = findPs.executeQuery()) {
                        while (rs.next()) {
                            slotsToRestore.add(rs.getInt("Slot_ID"));
                        }
                    }
                }

                // 3. Restore Available_Slots count for each confirmed booking
                if (!slotsToRestore.isEmpty()) {
                    try (PreparedStatement restorePs = conn.prepareStatement(restoreSlotSql)) {
                        for (int slotId : slotsToRestore) {
                            restorePs.setInt(1, slotId);
                            restorePs.executeUpdate();
                        }
                    }
                }

                // 4. Delete all bookings for this user
                try (PreparedStatement delBookingsPs = conn.prepareStatement(deleteBookingsSql)) {
                    delBookingsPs.setInt(1, userId);
                    delBookingsPs.executeUpdate();
                }

                // 5. Delete the user account
                boolean deleted;
                try (PreparedStatement delUserPs = conn.prepareStatement(deleteUserSql)) {
                    delUserPs.setInt(1, userId);
                    deleted = delUserPs.executeUpdate() > 0;
                }

                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to delete user and associated bookings.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error while deleting user.", e);
        }
    }

    public int countMembers() {
        String sql = "SELECT COUNT(*) FROM USER WHERE Role = 'USER'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count members.", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("User_ID"),
            rs.getString("Name"),
            rs.getString("Email"),
            rs.getString("Phone"),
            rs.getString("Password"),
            rs.getString("Role")
        );
    }
}
