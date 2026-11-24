<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="card auth-card">
            <h2>Member Login</h2>
            <p class="muted" style="margin-bottom: 1.25rem;">Sign in to view available gym slots and manage bookings.</p>

            <%
                String error = (String) request.getAttribute("error");
                if (error == null) error = request.getParameter("error");
                String success = request.getParameter("success");
            %>
            <% if (success != null) { %>
                <div class="alert alert-success"><%= HtmlUtil.escape(success) %></div>
            <% } %>
            <% if (error != null) { %>
                <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" required placeholder="rahul@example.com">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Log In</button>
            </form>

            <p class="muted" style="margin-top: 1rem; text-align: center;">
                New member? <a href="<%= request.getContextPath() %>/register.jsp">Create an account</a>
            </p>
        </div>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
