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
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                event.preventDefault();
                alert("A két jelszó nem egyezik!");
            }
        });
    }
});