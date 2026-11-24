<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Set, com.gymbooking.model.GymSlot, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Available Gym Slots</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="page-header">
            <div>
                <h1>Available Gym Slots</h1>
                <p class="muted">Select a date and reserve your preferred workout time.</p>
            </div>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/user/bookings">View My Bookings</a>
        </div>

        <%
            String error = (String) request.getAttribute("error");
            if (error == null) error = request.getParameter("error");
            String success = request.getParameter("success");
            String selectedDate = (String) request.getAttribute("selectedDate");
            List<GymSlot> slots = (List<GymSlot>) request.getAttribute("slots");
            Set<Integer> bookedSlotIds = (Set<Integer>) request.getAttribute("bookedSlotIds");
        %>
        <% if (success != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.escape(success) %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
        <% } %>

        <div class="card">
            <form class="filter-bar" action="<%= request.getContextPath() %>/user/slots" method="get">
                <div>
                    <label for="date">Filter by Date</label>
                    <input type="date" id="date" name="date" value="<%= selectedDate != null ? HtmlUtil.escape(selectedDate) : "" %>">
                </div>
                <button type="submit" class="btn btn-primary">Filter</button>
                <a class="btn btn-secondary" href="<%= request.getContextPath() %>/user/slots">Show All Upcoming</a>
            </form>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Slot ID</th>
                            <th>Date</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Capacity</th>
                            <th>Available</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (slots == null || slots.isEmpty()) { %>
                        <tr>
                            <td colspan="7" class="muted" style="text-align: center;">No gym slots found for the selected filter.</td>
                        </tr>
                    <% } else {
                        for (GymSlot s : slots) {
                            boolean isFull = s.getAvailableSlots() <= 0;
                            boolean isBooked = bookedSlotIds != null && bookedSlotIds.contains(s.getSlotId());
                    %>
                        <tr>
                            <td>#<%= s.getSlotId() %></td>
                            <td><%= s.getSlotDate() %></td>
                            <td><%= s.getStartTime().toString().substring(0, 5) %></td>
                            <td><%= s.getEndTime().toString().substring(0, 5) %></td>
                            <td><%= s.getCapacity() %></td>
                            <td>
                                <% if (isFull) { %>
                                    <span class="badge badge-danger">Full (0)</span>
                                <% } else if (s.getAvailableSlots() <= 3) { %>
                                    <span class="badge badge-warning"><%= s.getAvailableSlots() %> left</span>
                                <% } else { %>
                                    <span class="badge badge-success"><%= s.getAvailableSlots() %> available</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (isBooked) { %>
                                    <span class="badge badge-neutral" style="padding: 0.4rem 0.75rem; font-size: 0.85rem;">Already Booked</span>
                                <% } else { %>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/user/book" method="post">
                                        <input type="hidden" name="slotId" value="<%= s.getSlotId() %>">
                                        <button type="submit" class="btn btn-primary btn-sm" <%= isFull ? "disabled" : "" %>>
                                            <%= isFull ? "Sold Out" : "Book Slot" %>
                                        </button>
                                    </form>
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
