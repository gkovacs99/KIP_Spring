
const departmentSelect = document.getElementById("departmentId");
const doctorSelect = document.getElementById("doctorId");
const slotSelect = document.getElementById("startTime");
const bookButton = document.getElementById("bookButton");

const doctorsUrl = "/appointments/doctors";
const slotsUrl = "/appointments/slots";


function resetSelect(select, message) {
    select.innerHTML = "";

    const option = document.createElement("option");

    option.value = "";
    option.textContent = message;
    option.disabled = true;
    option.selected = true;

    select.appendChild(option);
    select.disabled = true;
}


departmentSelect.addEventListener("change", async function () {

    const departmentId = this.value;

    resetSelect(doctorSelect, "Orvosok betöltése...");
    resetSelect(slotSelect, "Először válasszon orvost...");

    bookButton.disabled = true;

    if (!departmentId) {
        return;
    }

    try {
        const response = await fetch(
            `${doctorsUrl}?departmentId=${encodeURIComponent(departmentId)}`
        );

        if (!response.ok) {
            throw new Error(`HTTP hiba: ${response.status}`);
        }

        const doctors = await response.json();

        resetSelect(doctorSelect, "Válasszon orvost...");

        if (doctors.length === 0) {
            resetSelect(doctorSelect, "Nincs elérhető orvos.");
            return;
        }

        doctors.forEach(doctor => {
            const option = document.createElement("option");

            option.value = doctor.id;
            option.textContent = doctor.name;

            doctorSelect.appendChild(option);
        });

        doctorSelect.disabled = false;

    } catch (error) {
        resetSelect(doctorSelect, "Nem sikerült betölteni az orvosokat.");
        console.error("Orvosok lekérése sikertelen:", error);
    }
});


doctorSelect.addEventListener("change", async function () {

    const doctorId = this.value;

    resetSelect(slotSelect, "Szabad időpontok betöltése...");
    bookButton.disabled = true;

    if (!doctorId) {
        return;
    }

    try {
        const response = await fetch(
            `${slotsUrl}?doctorId=${encodeURIComponent(doctorId)}`
        );

        if (!response.ok) {
            throw new Error(`HTTP hiba: ${response.status}`);
        }

        const slots = await response.json();

        resetSelect(slotSelect, "Válasszon időpontot...");

        if (slots.length === 0) {
            resetSelect(slotSelect, "Nincs szabad időpont.");
            return;
        }

        slots.forEach(slot => {
            const option = document.createElement("option");

            option.value = slot.startTime;
            option.textContent = slot.label;

            slotSelect.appendChild(option);
        });

        slotSelect.disabled = false;

    } catch (error) {
        resetSelect(slotSelect, "Nem sikerült betölteni az időpontokat.");
        console.error("Szabad időpontok lekérése sikertelen:", error);
    }
});


slotSelect.addEventListener("change", function () {
    bookButton.disabled = !this.value;
});
