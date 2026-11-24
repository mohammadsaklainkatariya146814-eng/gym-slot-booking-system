<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.gymbooking.model.Booking, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Bookings - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
            Integer filterSlotId = (Integer) request.getAttribute("filterSlotId");
        %>

        <div class="page-header">
            <div>
                <h1><%= filterSlotId != null ? "Bookings for Slot #" + filterSlotId : "All Bookings" %></h1>
                <p class="muted">
                    <%= filterSlotId != null 
                        ? "Showing members who reserved Slot #" + filterSlotId + "." 
                        : "Inspect member reservations and cancel bookings when necessary." %>
                </p>
            </div>
            <% if (filterSlotId != null) { %>
                <a class="btn btn-secondary" href="<%= request.getContextPath() %>/admin/bookings">Show All Bookings</a>
            <% } %>
        </div>
        <% if (success != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.escape(success) %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
        <% } %>

        <div class="card">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Booking ID</th>
                            <th>Member</th>
                            <th>Phone</th>
                            <th>Slot Date</th>
                            <th>Time</th>
                            <th>Booked On</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (bookings == null || bookings.isEmpty()) { %>
                        <tr><td colspan="8" class="muted" style="text-align:center;">No bookings found.</td></tr>
                    <% } else {
                        for (Booking b : bookings) {
                            boolean confirmed = "CONFIRMED".equalsIgnoreCase(b.getStatus());
                    %>
                        <tr>
                            <td>#<%= b.getBookingId() %></td>
                            <td>
                                <strong><%= HtmlUtil.escape(b.getUserName()) %></strong><br>
                                <span class="muted"><%= HtmlUtil.escape(b.getUserEmail()) %></span>
                            </td>
                            <td><%= HtmlUtil.escape(b.getUserPhone()) %></td>
                            <td><%= b.getSlotDate() %></td>
                            <td><%= b.getStartTime().toString().substring(0, 5) %> - <%= b.getEndTime().toString().substring(0, 5) %></td>
                            <td><%= b.getBookingDate() %></td>
                            <td>
                                <span class="badge <%= confirmed ? "badge-success" : "badge-neutral" %>">
                                    <%= HtmlUtil.escape(b.getStatus()) %>
                                </span>
                            </td>
                            <td>
                                <% if (confirmed) { %>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/admin/bookings" method="post">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
                                        <% if (filterSlotId != null) { %>
                                            <input type="hidden" name="slotId" value="<%= filterSlotId %>">
                                        <% } %>
                                        <button type="submit" class="btn btn-danger btn-sm"
                                                data-confirm="Cancel booking #<%= b.getBookingId() %>?">
                                            Cancel
                                        </button>
                                    </form>
                                <% } else { %>
                                    <span class="muted">&mdash;</span>
                                <% } %>
                            </td>
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
