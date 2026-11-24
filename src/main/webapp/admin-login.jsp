<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="card auth-card">
            <h2>Administrator Login</h2>
            <p class="muted" style="margin-bottom: 1.25rem;">Restricted to authorized gym administrators.</p>

            <%
                String error = (String) request.getAttribute("error");
                if (error == null) error = request.getParameter("error");
            %>
            <% if (error != null) { %>
                <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/admin/login" method="post">
                <div class="form-group">
                    <label for="email">Admin Email</label>
                    <input type="email" id="email" name="email" required placeholder="admin@gym.com">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-secondary" style="width: 100%;">Sign In to Admin Panel</button>
            </form>
        </div>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
