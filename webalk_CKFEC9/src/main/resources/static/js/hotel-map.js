// Térkép inicializálása a megadott hotel címhez
function initializeHotelMap(hotelAddress) {
    // Alapértelmezett pozíció, ha a geokódolás sikertelen
    let lat = 40.99046;
    let lon = 29.02916;

    // Térkép inicializálása
    let map = L.map('map').setView([lat, lon], 13);

    // OpenStreetMap csempék hozzáadása a térképhez
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> közreműködők'
    }).addTo(map);

    // Érvényes bemenet ellenőrzése
    if (!hotelAddress || hotelAddress.trim() === "") {
        alert("Érvénytelen cím lett megadva.");
        return;
    }

    // Cím geokódolása
    fetch(`https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(hotelAddress)}`)
        .then(response => response.json())
        .then(data => {
            if (data[0]) {
                lat = parseFloat(data[0].lat);
                lon = parseFloat(data[0].lon);
                map.setView([lat, lon], 13);
                L.marker([lat, lon])
                    .addTo(map)
                    .bindPopup(hotelAddress)
                    .openPopup();
            } else {
                alert("A helyszín nem található. Kérjük, ellenőrizze a címet.");
            }
        })
        .catch(error => {
            console.error("A geokódolás sikertelen:", error);
            alert("Hiba történt a cím keresése közben.");
        });
}
