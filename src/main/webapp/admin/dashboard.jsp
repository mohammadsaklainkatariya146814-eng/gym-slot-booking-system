<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.gymbooking.model.Booking, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="page-header">
            <div>
                <h1>Admin Dashboard</h1>
                <p class="muted">Overview of gym members, scheduled slots, and booking activity.</p>
            </div>
            <div>
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/admin/slots">Manage Slots</a>
                <a class="btn btn-secondary" href="<%= request.getContextPath() %>/admin/users">Manage Users</a>
            </div>
        </div>

        <section class="grid-4">
            <div class="stat-card">
                <div class="stat-label">Registered Members</div>
                <div class="stat-value"><%= request.getAttribute("totalMembers") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Gym Slots</div>
                <div class="stat-value"><%= request.getAttribute("totalSlots") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Confirmed Bookings</div>
                <div class="stat-value"><%= request.getAttribute("confirmedBookings") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Cancelled Bookings</div>
                <div class="stat-value"><%= request.getAttribute("cancelledBookings") %></div>
            </div>
        </section>

        <div class="card">
            <div class="page-header" style="margin-bottom: 1rem;">
                <h2>Recent Bookings</h2>
                <a class="btn btn-secondary btn-sm" href="<%= request.getContextPath() %>/admin/bookings">View All</a>
            </div>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Member</th>
                            <th>Slot Date &amp; Time</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        List<Booking> recent = (List<Booking>) request.getAttribute("recentBookings");
                        if (recent == null || recent.isEmpty()) {
                    %>
                        <tr><td colspan="4" class="muted" style="text-align:center;">No bookings recorded yet.</td></tr>
                    <% } else {
                        int limit = Math.min(recent.size(), 8);
                        for (int i = 0; i < limit; i++) {
                            Booking b = recent.get(i);
                            boolean confirmed = "CONFIRMED".equalsIgnoreCase(b.getStatus());
                    %>
                        <tr>
                            <td>#<%= b.getBookingId() %></td>
                            <td><%= HtmlUtil.escape(b.getUserName()) %> (<%= HtmlUtil.escape(b.getUserEmail()) %>)</td>
                            <td><%= b.getSlotDate() %> | <%= b.getStartTime().toString().substring(0, 5) %> - <%= b.getEndTime().toString().substring(0, 5) %></td>
                            <td><span class="badge <%= confirmed ? "badge-success" : "badge-neutral" %>"><%= HtmlUtil.escape(b.getStatus()) %></span></td>
                        </tr>
                    <%  }
                       } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
