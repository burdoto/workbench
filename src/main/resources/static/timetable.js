function parameterValue(name) {
    // noinspection JSAssignmentUsedAsCondition
    if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search)) return decodeURIComponent(name[1]);
}

function init() {
    for (let filter of $('.filter')) {
        let name = filter.id.substring(7)
        let value = parameterValue(name);
        if (value !== undefined) filter.value = value.replace(/(\+|%20)/g, ' ')
    }

    let date = new Date()
    for (let input of $('.input-datetime')) {
        input.value = toLocalISOString(date)
    }

    populateCustomerNames()
    populateDepartmentNames()
}

function populateCustomerNames() {
    populateSelection($('#input-customerName')[0], '/api/customers/names')
}

function populateDepartmentNames() {
    let customer = $('#input-customerName')[0].value
    if (customer === undefined || customer === '') return
    populateSelection($('#input-departmentName')[0], '/api/customers/' + customer + '/departments')
}

async function populateSelection(selectTag, optionsUrl) {
    selectTag.innerHTML = ''

    let elements = await fetch(optionsUrl).then(response => response.json())
    for (let element of elements) {
        let option = document.createElement('option')
        option.innerText = element
        option.value = element
        selectTag.appendChild(option)
    }
}

function toLocalISOString(date) {
    const localDate = new Date(date - date.getTimezoneOffset() * 60000); //offset in milliseconds. Credit https://stackoverflow.com/questions/10830357/javascript-toisostring-ignores-timezone-offset

    // Optionally remove second/millisecond if needed
    localDate.setSeconds(null);
    localDate.setMilliseconds(null);
    return localDate.toISOString().slice(0, -1);
}

function submitCreateEntry() {
    var data = $('form').serializeArray()

    fetch({
        method: 'POST', url: 'api/timetableEntries/create', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify(data)
    })
}
