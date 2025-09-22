function createCustomer() {
    let customer = prompt('Kundenname:')
    createDepartment(customer)
}

function createDepartment(customer) {
    let department = prompt('Abteilungsname:', '')

    fetch('/api/customers/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: customer,
            department: department
        })
    }).then(response => {
        if (response.ok) window.location.reload()
        alert('Interner Fehler')
    })
}
