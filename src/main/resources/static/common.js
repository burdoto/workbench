function isNameSane(name) {
    if (!/[a-zA-Z0-9 -_]/.test(name)) {
        alert('Name darf nur Buchstaben, Zahlen, Leerzeichen und - oder _ enthalten')
        return false
    }
    return true
}

async function initCommon() {
}
