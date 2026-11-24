<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="card auth-card">
            <h2>Member Registration</h2>
            <p class="muted" style="margin-bottom: 1.25rem;">Create your account to start booking gym slots.</p>

            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
            <% } %>

            <form id="registerForm" action="<%= request.getContextPath() %>/register" method="post">
                <div class="form-group">
                    <label for="name">Full Name</label>
                    <input type="text" id="name" name="name" required placeholder="e.g., Rahul Sharma">
                </div>
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" required placeholder="you@example.com">
                </div>
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" required placeholder="9876543210">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required minlength="6" placeholder="Minimum 6 characters">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Create Account</button>
            </form>

            <p class="muted" style="margin-top: 1rem; text-align: center;">
                Already registered? <a href="<%= request.getContextPath() %>/login.jsp">Log in here</a>
            </p>
        </div>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
