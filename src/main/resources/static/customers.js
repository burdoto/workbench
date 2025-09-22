function createCustomer() {
    let customer = prompt('Kundenname:')

    fetch('/api/customers/create', {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: customer
    }).then(handleResponse)
}

function createDepartment(customer) {
    let department = prompt('Abteilungsname:', '')

    fetch(`/api/customers/${customer}/create`, {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: department
    }).then(handleResponse)
}

function handleResponse(response) {
    if (response.ok) window.location.reload()
    alert('Interner Fehler')
}
