document.addEventListener("DOMContentLoaded", function () {
    // Registration password match validation
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", function (e) {
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            const phone = document.getElementById("phone").value.trim();

            if (password.length < 6) {
                e.preventDefault();
                alert("Password must be at least 6 characters long.");
                return;
            }
            if (password !== confirmPassword) {
                e.preventDefault();
                alert("Passwords do not match.");
                return;
            }
            if (!/^[0-9+\-\s]{7,15}$/.test(phone)) {
                e.preventDefault();
                alert("Please enter a valid phone number.");
            }
        });
    }

    // Slot form time validation
    const slotForm = document.getElementById("slotForm");
    if (slotForm) {
        slotForm.addEventListener("submit", function (e) {
            const startTime = document.getElementById("startTime").value;
            const endTime = document.getElementById("endTime").value;
            const capacity = parseInt(document.getElementById("capacity").value, 10);

            if (startTime && endTime && endTime <= startTime) {
                e.preventDefault();
                alert("End time must be later than start time.");
                return;
            }
            if (isNaN(capacity) || capacity <= 0) {
                e.preventDefault();
                alert("Capacity must be a positive number.");
            }
        });
    }

    // Confirmation dialog for destructive actions
    document.querySelectorAll("[data-confirm]").forEach(function (el) {
        el.addEventListener("click", function (e) {
            const message = el.getAttribute("data-confirm");
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
