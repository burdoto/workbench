function createCustomer() {
    let customer = prompt('Kundenname:')
    if (!isNameSane(customer)) return;

    fetch('/api/customers/create', {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: customer
    }).then(handleResponse)
}

function createDepartment(customer) {
    let department = prompt('Abteilungsname:', '')
    if (!isNameSane(department)) return;

    fetch(`/api/customers/${customer}/departments`, {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: department
    }).then(handleResponse)
}

function removeDepartment(customer, department) {
    if (!confirm(`Abteilung ${department} von ${customer} entfernen?`)) return

    fetch(`/api/customers/${customer}/departments`, {
        method: 'DELETE', headers: {
            'Content-Type': 'application/json'
        }, body: department
    }).then(handleResponse)
}

async function handleResponse(response) {
    if (response.ok) window.location.reload(); else alert('Interner Fehler:\n' + await response.text())
}
