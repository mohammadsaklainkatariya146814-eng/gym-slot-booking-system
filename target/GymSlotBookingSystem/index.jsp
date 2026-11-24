<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gym Slot Booking System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <section class="hero">
            <h1>Book Your Gym Workout Slot Online</h1>
            <p>
                Reserve your preferred training time in seconds, track live slot availability,
                and prevent gym overcrowding with real-time capacity management.
            </p>
            <div class="hero-actions">
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/register.jsp">Create Account</a>
                <a class="btn btn-outline" href="<%= request.getContextPath() %>/login.jsp">Member Login</a>
                <a class="btn btn-outline" href="<%= request.getContextPath() %>/admin-login.jsp">Admin Portal</a>
            </div>
        </section>

        <section class="grid-3">
            <div class="card">
                <h3>Real-Time Availability</h3>
                <p class="muted">
                    View upcoming gym dates, session timings, and exact remaining spots before booking.
                </p>
            </div>
            <div class="card">
                <h3>Overbooking Prevention</h3>
                <p class="muted">
                    Transactional database row locking guarantees no slot ever exceeds its configured capacity.
                </p>
            </div>
            <div class="card">
                <h3>Complete Admin Control</h3>
                <p class="muted">
                    Administrators can add, update, or remove slots, manage members, and monitor every booking.
                </p>
            </div>
        </section>
    </main>

    <jsp:include page="/includes/footer.jsp" />
</body>
</html>
