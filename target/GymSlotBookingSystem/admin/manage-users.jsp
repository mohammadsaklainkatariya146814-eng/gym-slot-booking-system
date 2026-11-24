<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.gymbooking.model.User, com.gymbooking.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/includes/navbar.jsp" />

    <main class="container">
        <div class="page-header">
            <div>
                <h1>Manage Users</h1>
                <p class="muted">View registered members and manage accounts.</p>
            </div>
        </div>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            List<User> users = (List<User>) request.getAttribute("users");
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
                            <th>User ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (users == null || users.isEmpty()) { %>
                        <tr><td colspan="6" class="muted" style="text-align:center;">No users found.</td></tr>
                    <% } else {
                        for (User u : users) { %>
                        <tr>
                            <td>#<%= u.getUserId() %></td>
                            <td><%= HtmlUtil.escape(u.getName()) %></td>
                            <td><%= HtmlUtil.escape(u.getEmail()) %></td>
                            <td><%= HtmlUtil.escape(u.getPhone()) %></td>
                            <td>
                                <span class="badge <%= u.isAdmin() ? "badge-warning" : "badge-success" %>">
                                    <%= HtmlUtil.escape(u.getRole()) %>
                                </span>
                            </td>
                            <td>
                                <% if (!u.isAdmin()) { %>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/admin/users" method="post">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                        <button type="submit" class="btn btn-danger btn-sm"
                                                data-confirm="Delete user <%= HtmlUtil.escape(u.getName()) %> and their bookings?">
                                            Delete
                                        </button>
                                    </form>
                                <% } else { %>
                                    <span class="muted">Protected</span>
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
