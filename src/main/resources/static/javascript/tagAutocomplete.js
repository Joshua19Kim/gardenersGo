// got from stack overflow: https://stackoverflow.com/questions/30022728/perform-action-when-clicking-html5-datalist-option
// it is used to check if the user clicked an option from the autocomplete and if so to submit the form
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

