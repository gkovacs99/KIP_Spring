fetch("/test")
    .then(response => {
        console.log("HTTP státusz:", response.status);
        return response.text();
    })
    .then(text => {
        console.log("Kapott szöveg:", text);
    })
    .catch(error => {
        console.error("Fetch hiba:", error);
    });

document.addEventListener('DOMContentLoaded', function() {

    const roleSelect = document.getElementById('roleSelect');
    const employeeContainer = document.getElementById('employeeFieldContainer');
    const employeeInput = document.getElementById('employeeId');
    const tajContainer = document.getElementById('tajFieldContainer');
    const tajInput = document.getElementById('tajNumber');

    if(roleSelect) {
        roleSelect.addEventListener('change', function() {
            if (this.value === 'employee') {
                tajContainer.classList.add('d-none');
                tajInput.value = '';
                employeeContainer.classList.remove('d-none');

            } else if (this.value === 'patient') {
                employeeContainer.classList.add('d-none');
                employeeInput.value = '';
                tajContainer.classList.remove('d-none');

            } else {
                tajContainer.classList.add('d-none');
                tajInput.value = '';
                employeeContainer.classList.add('d-none');
                employeeInput.value = '';
            }
        });
    }

    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            event.preventDefault();

            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                alert("A két jelszó nem egyezik!");
                return;
            }

            const requestData = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                password: password,
                role: roleSelect.value,
                tajNumber: tajInput.value || null,
                employeeId: employeeInput.value || null
            };

            fetch("/api/v1/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    if (response.ok) {
                        alert("Sikeres regisztráció!");
                        window.location.href = "/login";
                    } else {
                        alert("Hiba a regisztráció során. Státuszkód: " + response.status);
                    }
                })
                .catch(error => {
                    alert("Hálózati hiba történt, nem sikerült elérni a szervert.");
                });
        });
    }
});