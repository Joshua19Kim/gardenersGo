const scanningLocationToggle = document.getElementById('locationToggle');
const manualAddLocationToggle = document.getElementById('manualAddLocationToggle');

const geolocationUpdateMssg = document.getElementById("geolocationUpdateMssg");
const manualAddGeolocationUpdateMssg = document.getElementById("manualAddGeolocationUpdateMssg");


function refreshFields() {
    document.getElementById('name').value = "";
    document.getElementById('scanning-description').value = "";
    document.getElementById('nameError').innerText = '';
    document.getElementById('descriptionError').innerText = '';
    disableLocationInput(false);
    scanningAutocompleteResults.style.display = 'block';
    scanningLocation.value = "";
    geolocationUpdateMssg.innerHTML = '';
    manualAddGeolocationUpdateMssg.innerHTML = '';
    document.getElementById('locationToggle').checked = false;
    locationUpdateMssg.innerHTML = "";
    manualAddGeolocationUpdateMssg.innerHTML = "";
    document.getElementById('scanningCharacterCount').innerText = '0';
    document.getElementById('name').classList.remove('is-invalid');
    document.getElementById('scanning-description').classList.remove('is-invalid');
    plantLat.value = "";
    plantLon.value = "";
}



function updateMessage(checked, toggle) {
    if (this.checked) {
        toggle.innerHTML = waitingSpinnerHtml;
        if (navigator.geolocation) {
            if (toggle === scanningLocationToggle) {
                console.log("hahaha");
                navigator.geolocation.getCurrentPosition(setCoordinates, showError);
            } else {
                console.log("hahaha222");
                navigator.geolocation.getCurrentPosition(setCoordinates, showErrorForManualAdd);
            }
            disableLocationInput(true);
        } else {
            toggle.innerHTML = "Geolocation is not supported by this browser.";
            disableLocationInput(false);
        }
    } else {
        toggle.innerHTML = '';
        disableLocationInput(false);
        scanningAutocompleteResults.style.display = 'block';
    }
    toggle.style.color = "green";
}

scanningLocationToggle.addEventListener('change', function() {
    updateMessage(this.checked, scanningLocationToggle)

});

manualAddLocationToggle.addEventListener('change', function() {
    updateMessage(this.checked, manualAddLocationToggle)

});


function showErrorForManualAdd(error) {
    manualAddLocationToggle.checked = false;
    disableLocationInput(false);
    autocompleteResults.style.display = 'block';
    manualAddLocationUpdateMssg.style.color = "red";
    switch(error.code) {
        case error.PERMISSION_DENIED:
            manualAddLocationUpdateMssg.innerHTML = "Current Location permission denied."
            break;
        case error.POSITION_UNAVAILABLE:
            manualAddLocationUpdateMssg.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            manualAddLocationUpdateMssg.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            manualAddLocationUpdateMssg.innerHTML = "An unknown error occurred."
            break;
    }
}

function showError(error) {
    scanningLocationToggle.checked = false;
    disableLocationInput(false);
    scanningAutocompleteResults.style.display = 'block';
    geolocationUpdateMssg.style.color = "red";
    switch(error.code) {
        case error.PERMISSION_DENIED:
            geolocationUpdateMssg.innerHTML = "Current Location permission denied."
            break;
        case error.POSITION_UNAVAILABLE:
            geolocationUpdateMssg.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            geolocationUpdateMssg.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            geolocationUpdateMssg.innerHTML = "An unknown error occurred."
            break;
    }
}

function setCoordinates(position) {
    if (document.getElementById("successModal").classList.contains("show")) {
        plantLat.value = position.coords.latitude.toString();
        plantLon.value = position.coords.longitude.toString();
        geolocationUpdateMssg.innerHTML = 'Current location saved.';
        manualAddGeolocationUpdateMssg.innerHTML = 'Current location saved.';

    }
}

// when user clicks 'use current location', disable the input field for searching location.
function disableLocationInput(disable) {
    scanningLocation.disabled = disable;
    if (disable) {
        locationUpdateMssg.innerHTML = "";
        manualAddLocationUpdateMssg.innerHTML = "";
        scanningLocation.value = "";
        scanningAutocompleteResults.style.display = 'none';
        scanningAutocompleteResults.classList.remove('visible');
        scanningLocation.classList.add('disabled');
    } else {
        scanningLocation.classList.remove('disabled');
        plantLat.value = '';
        plantLon.value = '';
    }
}
