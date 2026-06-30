document.addEventListener('DOMContentLoaded', function() {
    const fieldPassword = document.getElementById("password");
    const fieldRepeatPassword = document.getElementById("repeat-password");
    const fieldEmail = document.getElementById("email");
    const successButton = document.getElementById("success-register");

    successButton.disabled = true
    fieldEmail.addEventListener("input", handlerInputFields)
    fieldPassword.addEventListener("input", handlerInputFields);
    fieldRepeatPassword.addEventListener("input", handlerInputFields);
});

function handlerInputFields() {
    const successButton = document.getElementById("success-register");

    const email = document.getElementById("email").value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    let allowedPassword = true;
    const password = document.getElementById("password").value;
    const repeatPassword = document.getElementById("repeat-password").value;

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-length",
        password.length >= 8
    );

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-uppercase",
        /[A-Z]/.test(password)
    );

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-lowercase",
        /[a-z]/.test(password)
    );

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-digit",
        /\d/.test(password)
    );

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-special",
        /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(password)
    );

    allowedPassword = allowedPassword && checkFailedPassword(
        "rule-identical",
        password===repeatPassword && password !== ""
    );

    successButton.disabled = !allowedPassword || !emailRegex.test(email)
}

function checkFailedPassword(id, valid) {
    const rule = document.getElementById(id);

    if (valid) {
        rule.classList.add("valid");
    } else {
        rule.classList.remove("valid");
    }

    return valid;
}