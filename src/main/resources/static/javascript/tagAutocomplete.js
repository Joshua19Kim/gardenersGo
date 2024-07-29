const tagInput = document.getElementById('tag-input');
const tagForm = document.getElementById("hidden-tag-form");
const dataList = document.getElementById("tagList");
const tagContainer = document.getElementById('tag-container');
const gardenId = document.querySelector('input[name="gardenId"]').value;
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
        // tagForm.submit();
        submitTag(inputValue); //added
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

//original codes
// function submitTagOnEnter(event) {
//     if (event.key === 'Enter') {
//         event.preventDefault();
//         const tagInput = document.getElementById('tag-input').value.trim();
//         if (tagInput) {
//             console.log('Submitting tag:', tagInput);
//             // Set the hidden form inputs
//             document.getElementById('hidden-tag-input').value = tagInput;
//             document.getElementById('hidden-tag-form').submit();
//         } else {
//             console.log('No tag input to submit');
//         }
//     }
// }
//
// function submitTag() {
//     const tagInput = document.getElementById('tag-input').value.trim();
//     if (tagInput) {
//         console.log('Submitting tag:', tagInput);
//         // Set the hidden form inputs
//         document.getElementById('hidden-tag-input').value = tagInput;
//         document.getElementById('hidden-tag-form').submit();
//     } else {
//         console.log('No tag input to submit');
//     }
// }
















///temp code for Josh




function submitTagOnEnter(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        const tagInputValue = tagInput.value.trim();
        if (tagInputValue) {
            submitTag(tagInputValue);
        } else {
            console.log('No tag input to submit');
        }
    }
}

// New function to submit tag using fetch
function submitTag(tag) {
    if (tag) {
        fetch('/gardens/addTag', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: `tag-input=${encodeURIComponent(tag)}&gardenId=${gardenId}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    showError(data.error);
                } else {
                    refreshTags();
                    tagInput.value = ''; // Clear input
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Failed to add tag. Please try again.');
            });
    } else {
        console.log('No tag input to submit');
    }
}

function showError(message) {
    const errorContainer = document.getElementById('tag-error-container');
    errorContainer.textContent = message;
    errorContainer.style.display = 'block';
    setTimeout(() => {
        errorContainer.style.display = 'none';
    }, 3000);
}

// New functions to fetch and update tags
async function refreshTags() {
    const [allTags, gardenTags] = await Promise.all([fetchAllTags(), fetchGardenTags()]);
    updateTagContainer(gardenTags);
    updateTagDatalist(allTags);
}

async function fetchAllTags() {
    try {
        const response = await fetch(`/getAllTagsList?gardenId=${gardenId}`);
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Error fetching all tags:', error);
        return [];
    }
}

async function fetchGardenTags() {
    try {
        const response = await fetch(`/getTagList?gardenId=${gardenId}`);
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Error fetching garden tags:', error);
        return [];
    }
}

function updateTagContainer(tags) {
    const tagContent = document.createElement('div');
    tagContent.innerHTML = '<strong>Tags:</strong>';
    tags.forEach(tag => {
        const tagElement = document.createElement('span');
        tagElement.textContent = tag;
        tagElement.classList.add('tag');
        tagContent.appendChild(tagElement);
    });
    tagContainer.innerHTML = '';
    tagContainer.appendChild(tagContent);
}

function updateTagDatalist(allTags) {
    dataList.innerHTML = '';
    allTags.forEach(tag => {
        const option = document.createElement('option');
        option.value = tag;
        dataList.appendChild(option);
    });
}

// Initial load of tags
refreshTags();