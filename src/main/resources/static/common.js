function isNameSane(name) {
    if (!/[a-zA-Z0-9 -_]/.test(name)) {
        alert('Name darf nur Buchstaben, Zahlen, Leerzeichen und - oder _ enthalten')
        return false
    }
    return true
}

async function initCommon() {
}

async function populateSelection(tags, optionsUrl) {
    for (let selectTag of tags) {
        if (selectTag.tagName !== 'SELECT') continue
        selectTag.innerHTML = ''

        let elements = await fetch(optionsUrl).then(response => response.json())
        for (let element of elements) {
            let option = document.createElement('option')
            option.innerText = element
            option.value = element
            selectTag.appendChild(option)
        }
    }
}
