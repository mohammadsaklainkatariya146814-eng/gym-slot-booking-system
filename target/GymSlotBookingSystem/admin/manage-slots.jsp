<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.gymbooking.model.GymSlot, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Gym Slots - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="page-header">
            <div>
                <h1>Manage Gym Slots</h1>
                <p class="muted">Add new workout slots, adjust capacities, or remove schedules.</p>
            </div>
        </div>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            GymSlot editSlot = (GymSlot) request.getAttribute("editSlot");
            List<GymSlot> slots = (List<GymSlot>) request.getAttribute("slots");
            boolean isEdit = editSlot != null;
        %>
        <% if (success != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.escape(success) %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
        <% } %>

        <div class="card">
            <h2><%= isEdit ? "Update Gym Slot #" + editSlot.getSlotId() : "Add New Gym Slot" %></h2>
            <form id="slotForm" action="<%= request.getContextPath() %>/admin/slots" method="post">
                <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                <% if (isEdit) { %>
                    <input type="hidden" name="slotId" value="<%= editSlot.getSlotId() %>">
                <% } %>

                <div class="form-row">
                    <div class="form-group">
                        <label for="slotDate">Slot Date</label>
                        <input type="date" id="slotDate" name="slotDate" required
                               value="<%= isEdit ? editSlot.getSlotDate().toString() : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="startTime">Start Time</label>
                        <input type="time" id="startTime" name="startTime" required
                               value="<%= isEdit ? editSlot.getStartTime().toString().substring(0, 5) : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="endTime">End Time</label>
                        <input type="time" id="endTime" name="endTime" required
                               value="<%= isEdit ? editSlot.getEndTime().toString().substring(0, 5) : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="capacity">Slot Capacity</label>
                        <input type="number" id="capacity" name="capacity" min="1" max="500" required
                               value="<%= isEdit ? editSlot.getCapacity() : 15 %>">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary"><%= isEdit ? "Save Changes" : "Add Slot" %></button>
                <% if (isEdit) { %>
                    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/admin/slots">Cancel Edit</a>
                <% } %>
            </form>
        </div>

        <div class="card">
            <h2>All Gym Slots</h2>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Slot ID</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Capacity</th>
                            <th>Booked</th>
                            <th>Available</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (slots == null || slots.isEmpty()) { %>
                        <tr><td colspan="7" class="muted" style="text-align:center;">No gym slots configured yet.</td></tr>
                    <% } else {
                        for (GymSlot s : slots) { %>
                        <tr>
                            <td>#<%= s.getSlotId() %></td>
                            <td><%= s.getSlotDate() %></td>
                            <td><%= s.getStartTime().toString().substring(0, 5) %> - <%= s.getEndTime().toString().substring(0, 5) %></td>
                            <td><%= s.getCapacity() %></td>
                            <td>
                                <% if (s.getBookedCount() > 0) { %>
                                    <a href="<%= request.getContextPath() %>/admin/bookings?slotId=<%= s.getSlotId() %>"
                                       style="color: var(--primary-dark); font-weight: 600; text-decoration: underline;"
                                       title="View members who booked this slot">
                                        <%= s.getBookedCount() %> member<%= s.getBookedCount() > 1 ? "s" : "" %>
                                    </a>
                                <% } else { %>
                                    0
                                <% } %>
                            </td>
                            <td>
                                <span class="badge <%= s.getAvailableSlots() > 0 ? "badge-success" : "badge-danger" %>">
                                    <%= s.getAvailableSlots() %>
                                </span>
                            </td>
                            <td>
                                <a class="btn btn-secondary btn-sm"
                                   href="<%= request.getContextPath() %>/admin/slots?editId=<%= s.getSlotId() %>">Edit</a>
                                <form class="inline-form" action="<%= request.getContextPath() %>/admin/slots" method="post">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="slotId" value="<%= s.getSlotId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm"
                                            data-confirm="Delete slot #<%= s.getSlotId() %> and all associated bookings?">
                                        Delete
                                    </button>
                                </form>
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
