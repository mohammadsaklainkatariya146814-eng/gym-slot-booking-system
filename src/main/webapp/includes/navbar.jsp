<%@ page import="com.gymbooking.model.User, com.gymbooking.util.HtmlUtil" %>
<%
    User navUser = (User) session.getAttribute("currentUser");
    String ctx = request.getContextPath();
%>
<nav class="navbar">
    <a class="brand" href="<%= ctx %>/index.jsp">Gym<span>Slot</span></a>
    <div class="nav-links">
        <% if (navUser == null) { %>
            <a href="<%= ctx %>/index.jsp">Home</a>
            <a href="<%= ctx %>/login.jsp">Member Login</a>
            <a href="<%= ctx %>/register.jsp">Register</a>
            <a href="<%= ctx %>/admin-login.jsp">Admin Login</a>
        <% } else if (navUser.isAdmin()) { %>
            <a href="<%= ctx %>/admin/dashboard">Dashboard</a>
            <a href="<%= ctx %>/admin/slots">Manage Slots</a>
            <a href="<%= ctx %>/admin/users">Manage Users</a>
            <a href="<%= ctx %>/admin/bookings">All Bookings</a>
            <span class="nav-user">Admin: <%= HtmlUtil.escape(navUser.getName()) %></span>
            <a href="<%= ctx %>/logout">Logout</a>
        <% } else { %>
            <a href="<%= ctx %>/user/slots">Available Slots</a>
            <a href="<%= ctx %>/user/bookings">My Bookings</a>
            <span class="nav-user">Hi, <%= HtmlUtil.escape(navUser.getName()) %></span>
            <a href="<%= ctx %>/logout">Logout</a>
        <% } %>
    </div>
</nav>
