const tagInput = document.getElementById('tag-input');
const tagForm = document.getElementById("hidden-tag-form");
const dataList = document.getElementById("tagList");
let removedOption = "";
let keypress = false;
tagInput.addEventListener("keydown", (e) => {
    if(e.key) {
        keypress = true;
    }
});
tagForm.addEventListener('input', (e) => {
    const inputValue = tagInput.value;
    const options = dataList.options
    if (keypress === false) {
        document.getElementById('hidden-tag-input').value = inputValue;
        tagForm.submit();
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

function transformToInput() {
    document.getElementById('add-tag').style.display = 'none';
    document.getElementById('tag-input').style.display = 'block';
    document.getElementById('submit-tag').style.display = 'block';
    document.getElementById('tag-input').focus();
}

// original codes
function submitTagOnEnter(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        const tagInput = document.getElementById('tag-input').value.trim();
        if (tagInput) {
            console.log('Submitting tag:', tagInput);
            // Set the hidden form inputs
            document.getElementById('hidden-tag-input').value = tagInput;
            document.getElementById('hidden-tag-form').submit();
        } else {
            console.log('No tag input to submit');
        }
    }
}

function submitTag() {
    const tagInput = document.getElementById('tag-input').value.trim();
    if (tagInput) {
        console.log('Submitting tag:', tagInput);
        // Set the hidden form inputs
        document.getElementById('hidden-tag-input').value = tagInput;
        document.getElementById('hidden-tag-form').submit();
    } else {
        console.log('No tag input to submit');
    }
}

// function fetchUniqueTagsList(gardenId) {
//     return fetch(`../getUniqueTagsList?gardenId=${encodeURIComponent(gardenId)}`)
//         .then(response => response.json())
//         .catch(error => {
//             console.error('Error:', error);
//             return [];
//         });
// }
//
// function fetchTagsList(gardenId) {
//     return fetch(`../getTagList?gardenId=${encodeURIComponent(gardenId)}`)
//         .then(response => response.json())
//         .catch(error => {
//             console.error('Error:', error);
//             return [];
//         });
// }