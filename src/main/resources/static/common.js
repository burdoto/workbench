function isNameSane(name) {
    if (!/[a-zA-Z0-9 -_]/.test(name)) {
        alert('Name darf nur Buchstaben, Zahlen, Leerzeichen und - oder _ enthalten')
        return false
    }
    return true
}

async function initCommon() {
    for (let tag of $('.ui-collapse-button')) {
        tag.onclick = function () {
            let id = tag.id.substring(4)
            let box = $('#' + id)[0]
            box.style.display = box.style.display === 'none' || box.style.display === '' ? 'block' : 'none'
        }
    }
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
