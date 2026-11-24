<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" language="java" %>
<%@ page import="com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="card auth-card" style="text-align: center;">
            <h2>Something Went Wrong</h2>
            <p class="muted" style="margin: 1rem 0;">
                <%= exception != null && exception.getMessage() != null
                    ? HtmlUtil.escape(exception.getMessage())
                    : "The requested page could not be processed. Please try again." %>
            </p>
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/index.jsp">Return Home</a>
        </div>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
