<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.gymbooking.model.Booking, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="page-header">
            <div>
                <h1>My Bookings</h1>
                <p class="muted">Track your reserved gym slots or cancel if your plans change.</p>
            </div>
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/user/slots">Book New Slot</a>
        </div>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
        %>
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
                            <th>Slot Date</th>
                            <th>Time Window</th>
                            <th>Booked On</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (bookings == null || bookings.isEmpty()) { %>
                        <tr>
                            <td colspan="6" class="muted" style="text-align: center;">You have not booked any gym slots yet.</td>
                        </tr>
                    <% } else {
                        for (Booking b : bookings) {
                            boolean confirmed = "CONFIRMED".equalsIgnoreCase(b.getStatus());
                    %>
                        <tr>
                            <td>#<%= b.getBookingId() %></td>
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
                                    <form class="inline-form" action="<%= request.getContextPath() %>/user/cancel-booking" method="post">
                                        <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
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
