-- ============================================================
-- Gym Slot Booking System - MySQL Database Script
-- Import via XAMPP / phpMyAdmin or MySQL CLI
-- ============================================================

CREATE DATABASE IF NOT EXISTS gym_booking_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gym_booking_db;

DROP TABLE IF EXISTS BOOKING;
DROP TABLE IF EXISTS GYM_SLOT;
DROP TABLE IF EXISTS USER;

-- 1. USER Table
CREATE TABLE USER (
    User_ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(120) NOT NULL UNIQUE,
    Phone VARCHAR(20) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL DEFAULT 'USER',
    CONSTRAINT chk_user_role CHECK (Role IN ('USER', 'ADMIN'))
) ENGINE=InnoDB;

-- 2. GYM_SLOT Table
CREATE TABLE GYM_SLOT (
    Slot_ID INT AUTO_INCREMENT PRIMARY KEY,
    Slot_Date DATE NOT NULL,
    Start_Time TIME NOT NULL,
    End_Time TIME NOT NULL,
    Capacity INT NOT NULL,
    Available_Slots INT NOT NULL,
    CONSTRAINT chk_slot_time CHECK (End_Time > Start_Time),
    CONSTRAINT chk_slot_capacity CHECK (Capacity > 0),
    CONSTRAINT chk_slot_available CHECK (Available_Slots >= 0 AND Available_Slots <= Capacity),
    UNIQUE KEY uq_slot_schedule (Slot_Date, Start_Time, End_Time)
) ENGINE=InnoDB;

-- 3. BOOKING Table
CREATE TABLE BOOKING (
    Booking_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Slot_ID INT NOT NULL,
    Booking_Date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT fk_booking_user FOREIGN KEY (User_ID)
        REFERENCES USER(User_ID) ON DELETE CASCADE,
    CONSTRAINT fk_booking_slot FOREIGN KEY (Slot_ID)
        REFERENCES GYM_SLOT(Slot_ID) ON DELETE CASCADE,
    CONSTRAINT chk_booking_status CHECK (Status IN ('CONFIRMED', 'CANCELLED'))
) ENGINE=InnoDB;

-- ============================================================
-- Seed Data (Passwords are SHA-256 hashed)
-- Admin Login : admin@gym.com      / admin123
-- Member Login: rahul@example.com  / user123
-- Member Login: priya@example.com  / user123
-- ============================================================

INSERT INTO USER (Name, Email, Phone, Password, Role) VALUES
('Gym Administrator', 'admin@gym.com', '9876543210',
 '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('Rahul Sharma', 'rahul@gmail.com', '9825011223',
 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER'),
('Priya Patel', 'priya@gmail.com', '9825044556',
 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER');

INSERT INTO GYM_SLOT (Slot_Date, Start_Time, End_Time, Capacity, Available_Slots) VALUES
(CURDATE(), '06:00:00', '07:00:00', 15, 14),
(CURDATE(), '07:30:00', '08:30:00', 20, 19),
(CURDATE(), '17:00:00', '18:00:00', 20, 20),
(CURDATE(), '18:30:00', '19:30:00', 15, 15),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '06:00:00', '07:00:00', 15, 15),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00', '19:00:00', 20, 20);

INSERT INTO BOOKING (User_ID, Slot_ID, Status) VALUES
(2, 1, 'CONFIRMED'),
(3, 2, 'CONFIRMED');
