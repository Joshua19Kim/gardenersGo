var form = document.getElementById('plantForm');
var fileInput = document.getElementById('fileInput');
var currentImage = document.getElementById('currentImage');
var addPlantModal = document.getElementById('addPlantModal');
var plantDateInput = document.getElementById('plantDate');
const successAlert = document.getElementById('successAlert');
const manualAddLocation = document.getElementById('manualAddLocation');
const manualAddAutocompleteResults = document.getElementById('manual-add-autocomplete-results');
var today = new Date();

currentImage.src = `${getBaseUrl()}/images/placeholder.jpg`;

const manualAddLocationUpdateMssg = document.getElementById("manualAddLocationUpdateMssg") || null;
const manualAddGeolocationUpdateMssg = document.getElementById("manualAddGeolocationUpdateMssg");
const manualPlantLat = document.getElementById('manualPlantLat');
const manualPlantLon = document.getElementById('manualPlantLon');

var errorOccurred = document.getElementById("errorOccurred").getAttribute("data-value");
window.onload = function() {
    console.log(errorOccurred)
    if(errorOccurred === 'true') {
        var modal = bootstrap.Modal.getOrCreateInstance(addPlantModal);
        modal.show();
    }
    updateCharacterCount();
}

// clears all the fields when the modal is closed
addPlantModal.addEventListener('hide.bs.modal', function(event) {
    errorOccurred = false;

    const addPlantForm = document.getElementById('plantForm')

    const invalidInputs = addPlantForm.querySelectorAll('.is-invalid');
    invalidInputs.forEach((input) => {
        input.classList.remove('is-invalid');
    });

    const errorMessages = addPlantForm.querySelectorAll('span');
    errorMessages.forEach((errorMessage) => {
        if(errorMessage.id !== 'characterCountContainer' && errorMessage.id !== 'characterCount') {
            errorMessage.textContent = '';
        }
    })

    document.getElementById('description').value = '';
    document.getElementById('plantName').value = '';
    document.getElementById('scientificName').value = '';


});

// this will be triggered when the modal is about to open
addPlantModal.addEventListener('show.bs.modal', function (event) {
    fileInput.value = '';
    currentImage.src = `${getBaseUrl()}/images/placeholder.jpg`;

    if(errorOccurred !== 'true') {
        plantDateInput.value = today.getFullYear() + '-' +
            String(today.getMonth() + 1).padStart(2, '0') + '-' +
            String(today.getDate()).padStart(2, '0');
    }

});

document.getElementById("plantForm").addEventListener("keydown", function(event) {
    if (event.key === "Enter") {
        var activeElement = document.activeElement;
        if (activeElement.type === "date" || activeElement.tagName === "BUTTON") {
            return true;
        } else {
            event.preventDefault();
        }
    }
});

function handleImageChange() {
    var file = fileInput.files[0];
    if (file) {
        var reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('currentImage').src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

document.getElementById("plantForm").addEventListener("submit", function(event) {
    const dateInput = document.getElementById("plantDate");
    if (!(dateInput.checkValidity())) {
        event.preventDefault();
        document.getElementById("isDateInvalid").value = true;
        document.getElementById("plantForm").submit();
    } else {
        document.getElementById("isDateInvalid").value = false;
    }
});

document.getElementById('description').addEventListener('input', updateCharacterCount);

function updateCharacterCount() {
    var textarea = document.getElementById("description");
    var characterCount = document.getElementById("characterCount");
    characterCount.textContent = textarea.value.length;
}


/////////////////////////////////////////////////////////////////////
let plantNames = JSON.parse(document.getElementById('plantNamesJson').value || '[]');
let scientificNames = JSON.parse(document.getElementById('plantScientificNamesJson').value || '[]');

const plantNameInput = document.getElementById('plantName');
const scientificNameInput = document.getElementById('scientificName');

let removedOption = "";
let keypress = false;


const plantNamesList = document.getElementById('plantNamesList');
plantNames.forEach(name => {
    let option = document.createElement('option');
    option.value = name;
    option.addEventListener('click', function() {
        getPlantDetails(name, true);
    });
    plantNamesList.appendChild(option);
});

const scientificNamesList = document.getElementById('scientificNamesList');
scientificNames.forEach(name => {
    let option = document.createElement('option');
    option.value = name;
    scientificNamesList.appendChild(option);
});


plantNameInput.addEventListener('input', function() {
    const selectedValue = this.value;
    if (plantNames.includes(selectedValue)) {
        getPlantDetails(selectedValue, true);
    }
});

scientificNameInput.addEventListener('input', function() {
    const selectedValue = this.value;
    if (scientificNames.includes(selectedValue)) {
        getPlantDetails(selectedValue, false);
    }
});


plantNameInput.addEventListener("keydown", (e) => {
    if(e.key) {
        keypress = true;
    }
});

scientificNameInput.addEventListener("keydown", (e) => {
    if(e.key) {
        keypress = true;
    }
});


plantNameInput.addEventListener('change', (e) => {
    const inputValue = tagInput.value;
    const options = dataList.options
    if (keypress === false) {
        getPlantDetails(plantNameInput.value, true)
    }
    if(inputValue !== removedOption && removedOption !== "") {
        let newOption = document.createElement("option");
        newOption.value = removedOption;
        newOption.text = removedOption;
        dataList.appendChild(newOption);
        removedOption = "";
    }
    for (let i = 0; i < options.length; i++) {
        if (options[i].value === inputValue) {
            removedOption = options[i].value;
            options[i].remove();
            break;
        }
    }
    keypress = false;
});


function getPlantDetails(selectedPlantName, isPlantName) {
    if (manualAddLocation.value === "" && !manualAddLocation.disabled) {
        manualPlantLat.value = "";
        manualPlantLat.value = "";
    }
    const data = {
        name: selectedPlantName,
        isPlantName: isPlantName,
        isSpecieScientificName: !isPlantName,
        plantLatitude: manualPlantLat.value,
        plantLongitude: manualPlantLon.value
    };

    fetch(`${getBaseUrl()}/myCollection/autoPopulate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(plantDetailsList => {
            if (plantDetailsList.length > 0) {
                const plantDetails = plantDetailsList[0];
                document.getElementById('plantName').value = plantDetails.name || '';
                document.getElementById('scientificName').value = plantDetails.scientificName || '';
                document.getElementById('description').value = plantDetails.description || '';
                updateCharacterCount()
                document.getElementById('plantDate').value = convertToHtmlDate(plantDetails.dateUploaded) || '';
            } else {
                console.log('No plant details found');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function convertToHtmlDate(dateString) {
    const [day, month, year] = dateString.split('/');
    const date = new Date(year, month - 1, day);
    return date.toISOString().split('T')[0];
}


var addPlantButton = document.getElementById('addPlantButton');
var addPlantModalToOpen = new bootstrap.Modal(addPlantModal);

addPlantButton.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        addPlantModalToOpen.show();
    }
});

document.getElementById('manualAddLocationToggle').addEventListener('change', function() {
    if (this.checked) {
        manualAddGeolocationUpdateMssg.innerHTML = waitingSpinnerHtml;

        if (navigator.geolocation) {
            console.log("got here!")
            navigator.geolocation.getCurrentPosition(setCoordinates, showError);
            disableLocationInput(true);
        } else {
            console.log("gfailed")
            manualAddGeolocationUpdateMssg.innerHTML = "Geolocation is not supported by this browser.";
            disableLocationInput(false);
        }
    } else {
        manualAddGeolocationUpdateMssg.innerHTML = '';
        disableLocationInput(false);
        manualAddAutocompleteResults.style.display = 'block';
    }
    manualAddGeolocationUpdateMssg.style.color = "green";

});

function showError(error) {
    document.getElementById('manualAddLocationToggle').checked = false;
    disableLocationInput(false);
    manualAddAutocompleteResults.style.display = 'block';
    manualAddGeolocationUpdateMssg.style.color = "red";
    switch(error.code) {
        case error.PERMISSION_DENIED:
            manualAddGeolocationUpdateMssg.innerHTML = "Current Location permission denied."
            break;
        case error.POSITION_UNAVAILABLE:
            manualAddGeolocationUpdateMssg.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            manualAddGeolocationUpdateMssg.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            manualAddGeolocationUpdateMssg.innerHTML = "An unknown error occurred."
            break;
    }
}

function setCoordinates(position) {
    if (document.getElementById("successModal").classList.contains("show")) {
        manualPlantLat.value = position.coords.latitude.toString();
        manualPlantLon.value = position.coords.longitude.toString();
        manualAddGeolocationUpdateMssg.innerHTML = 'Current location saved.';

    }
}

// when user clicks 'use current location', disable the input field for searching location.
function disableLocationInput(disable) {
    manualAddLocation.disabled = disable;
    if (disable) {
        manualAddLocationUpdateMssg.innerHTML = "";
        manualAddLocation.value = "";
        manualAddAutocompleteResults.style.display = 'none';
        manualAddAutocompleteResults.classList.remove('visible');
        manualAddLocation.classList.add('disabled');
    } else {
        manualAddLocation.classList.remove('disabled');
        manualPlantLat.value = '';
        manualPlantLon.value = '';
    }}