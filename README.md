# Gym Slot Booking System

A complete Java Web Application built with **HTML, CSS, JavaScript, JSP, Servlets, JDBC, and MySQL** for Apache Tomcat and NetBeans.

## Features

- **User Registration & Login**: Account creation with duplicate-email checks, SHA-256 password hashing, `HttpSession` management, and role-based route protection via `AuthFilter`.
- **Gym Slot Management (Admin)**: Add, update, and delete gym slots with capacity validation that preserves active booking counts.
- **Slot Booking (Member)**: Browse upcoming slots, filter by date, view live availability badges, and book slots with transactional row-level locking (`SELECT ... FOR UPDATE`) to prevent overbooking.
- **Booking Management**: Members and admins can inspect bookings and cancel confirmed reservations, automatically restoring `Available_Slots`.
- **Admin Module**: Dedicated admin login, dashboard metrics, user management, slot management, and full booking oversight.

## Database Setup (XAMPP & phpMyAdmin)

1. Start **Apache** and **MySQL** in the XAMPP Control Panel.
2. Open **phpMyAdmin** (`http://localhost/phpmyadmin`).
3. Go to the **Import** tab, select `database/gym_booking.sql`, and click **Import**.
4. Verify that `gym_booking_db` contains the `USER`, `GYM_SLOT`, and `BOOKING` tables with sample seed records.

> If your MySQL `root` user has a password or runs on a different port, update `DB_URL`, `DB_USER`, and `DB_PASSWORD` in `src/main/java/com/gymbooking/util/DBConnection.java`.

## Running in NetBeans with Apache Tomcat

### Option 1: Open as a Maven Web Application (Recommended — NetBeans 11+)

1. Extract `GymSlotBookingSystem.zip`.
2. In NetBeans, select **File > Open Project** and choose the `GymSlotBookingSystem` folder.
3. Right-click the project > **Properties > Run**, and select your configured **Apache Tomcat 9** server.
4. Click **Run** (NetBeans will download `mysql-connector-j` via Maven, build the WAR, and deploy to Tomcat).

### Option 2: Classic NetBeans Ant Web Application (e.g., NetBeans 8.2)

1. In NetBeans, create **File > New Project > Java Web > Web Application** using Apache Tomcat 9.
2. Copy `src/main/java/com` into your project's `src/java/` folder.
3. Copy all files inside `src/main/webapp/` into your project's `web/` folder.
4. Add `mysql-connector-j-8.x.x.jar` to your project's **Libraries**.
5. Run the project.

> **Using Apache Tomcat 10 or 11?** Tomcat 10+ uses Jakarta EE (`jakarta.servlet.*`). Replace `javax.servlet.` with `jakarta.servlet.` across `src/main/java/com/gymbooking/` and update `javax.servlet-api` to `jakarta.servlet-api` in `pom.xml`.

## Default Seed Accounts

| Role   | Email               | Password   |
| ------ | ------------------- | ---------- |
| Admin  | `admin@gym.com`     | `admin123` |
| Member | `rahul@gmail.com` | `user123`  |
| Member | `priya@gmail.com` | `user123`  |
