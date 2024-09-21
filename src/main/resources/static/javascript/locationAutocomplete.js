//This code was referenced from code that were generated by ChatGPT and ClaudeAI

document.addEventListener('DOMContentLoaded', function () {
    const scanningAddressInput = document.getElementById('scanningLocation');
    const addressInput = document.getElementById('location') || null;
    const suburbInput = document.getElementById('suburb') || null;
    const cityInput = document.getElementById('city') || null;
    const countryInput = document.getElementById('country') || null;
    const postcodeInput = document.getElementById('postcode') || null;
    const autocompleteResults = document.getElementById('autocomplete-results') || null;
    const scanningAutocompleteResults = document.getElementById('scanning-autocomplete-results');
    const locationUpdateMssg = document.getElementById("locationUpdateMssg") || null;
    const plantLat = document.getElementById('plantLat') || null;
    const plantLon = document.getElementById('plantLon') || null;

    const xmlRequest = new XMLHttpRequest();
    let address;

    if (addressInput !=  null) {
        addressInput.addEventListener('input', function () {
            address = addressInput;
            getResult(autocompleteResults);
        });
    }

    scanningAddressInput.addEventListener('input', function () {
        address = scanningAddressInput;
        if (this.value.trim() === '') {
            plantLat.value = '';
            plantLon.value = '';
            locationUpdateMssg.innerHTML = "";
        } else {
            if (plantLat.value === '' && plantLon.value === '') {
                locationUpdateMssg.innerHTML = "Please select one of the options to verify the plant location."
                locationUpdateMssg.style.color = "blue";
            }
        }
        getResult(scanningAutocompleteResults);

    });



    function getResult(resultBox) {
        const inputValue = address.value.trim();
        if (inputValue.length > 0) {
            if (isValidInput(inputValue)) {
                fetchAutocomplete(inputValue)
                    .then(data => {
                        showAutocompleteResults(data, resultBox);
                    })
                    .catch(err => {
                        console.error(err);
                    });
            }
        } else {
            hideAutocompleteResults(resultBox);
        }
    }
    function fetchAutocomplete(query) {
        return new Promise((resolve, reject) => {
            xmlRequest.open('GET', '../sendRequest?query=' + encodeURIComponent(query));
            xmlRequest.onload = function () {
                if (xmlRequest.status === 200) {
                    const responseData = JSON.parse(xmlRequest.responseText);
                    resolve(responseData);
                    return responseData.json;
                } else {
                    console.error('Error:', xmlRequest.statusText);
                    reject(new Error('Network response was not ok'));
                }
            };
            xmlRequest.onerror = function () {
                reject(new Error('Request failed'));
            };
            xmlRequest.send();
        });
    }

    function showAutocompleteResults(results, resultBox) {
        resultBox.innerText = '';
        if (results.error) {
            const item = document.createElement('div');
            item.classList.add('autocomplete-item');
            item.textContent = "No matching location found, location-based services may not work"
            item.style.color = 'red';
            resultBox.appendChild(item);
        } else if (results.status === "Searching") {
            const item = document.createElement('div');
            item.classList.add('autocomplete-item');
            item.textContent = "Searching..."
            resultBox.appendChild(item);
            getResult(resultBox);
        }
        else {
            results.forEach(result => {
                const item = document.createElement('div');
                item.classList.add('autocomplete-item');
                item.classList.add('text-dark');
                const address = result.address;
                if(address.house_number === undefined) {
                    address.house_number = "";
                }
                const displayParts = [
                    address.house_number + " " + address.road,
                    address.suburb,
                    address.city,
                    address.postcode,
                    address.country
                ].filter(Boolean);

                item.textContent = displayParts.join(', ');

                item.addEventListener('mouseover', function() {
                    this.style.backgroundColor = '#f0f0f0';
                });
                item.addEventListener('mouseout', function() {
                    this.style.backgroundColor = '';
                });
                item.addEventListener('click', function () {
                    if (resultBox === autocompleteResults){
                        fillAddressDetails(result);
                    } else {
                        const addressInputValue = `${result.address.house_number || ''} ${result.address.road || ''} ${result.address.city || ''} ${result.address.country || ''}`;
                        if (scanningAddressInput.value !== '') {
                            plantLat.value = result.lat;
                            plantLon.value = result.lon;
                            locationUpdateMssg.innerHTML = "The location has been verified : <br/>" + addressInputValue;
                            locationUpdateMssg.style.color = "green";
                        }
                        scanningAddressInput.value = addressInputValue.trim();
                    }
                    hideAutocompleteResults(resultBox);
                });
                resultBox.appendChild(item);
            });
        }
        resultBox.classList.add('visible');
    }

    function hideAutocompleteResults(resultBox) {
        resultBox.innerText = '';
        resultBox.classList.remove('visible');
    }

    function fillAddressDetails(address) {
        const addressInputValue = `${address.address.house_number || ''} ${address.address.road || ''}`;
        addressInput.value = addressInputValue.trim();
        suburbInput.value = address.address.suburb || '';
        cityInput.value = address.address.city || '';
        countryInput.value = address.address.country || '';
        postcodeInput.value = address.address.postcode || '';
    }

    if (autocompleteResults) {
        document.addEventListener('click', function (event) {
            const target = event.target;
            if (!target.closest('.autocomplete-container')) {
                hideAutocompleteResults(autocompleteResults);
            }
        });
    }
    if (scanningAutocompleteResults) {
        document.addEventListener('click', function (event) {
            const target = event.target;
            if (!target.closest('.autocomplete-container')) {
                hideAutocompleteResults(scanningAutocompleteResults);
            }
        });
    }



    //This is added to accept only all the language and numeric characters plus some relevant special characters for addresses.
    function isValidInput(input) {
        return /^[\p{L}\p{N}\s\-',./()]+$/u.test(input);
    }
});