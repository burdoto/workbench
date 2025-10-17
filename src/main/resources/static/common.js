function isNameSane(name) {
    if (!/[a-zA-Z0-9 -_]/.test(name)) {
        alert('Name darf nur Buchstaben, Zahlen, Leerzeichen und - oder _ enthalten')
        return false
    }
    return true
}

async function applyPopupEdit() {
    let value = $('#input-newValue')[0].value;
    let response = await fetch('apply', {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify(value)
    })
    if (response.ok) window.location.href = new URL(window.location.href).searchParams.get('redirect_url'); else alert('Attribut konnte nicht bearbeitet werden:\n' + await response.text())
}

async function addEditPopupLink(caller) {
    let editTag = document.createElement('a')
    editTag.href = `/popup/edit/${dtype}/${info}/${property}/show`
    caller.innerHTML += editTag
}
