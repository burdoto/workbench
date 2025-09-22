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
    populateSelection($('#input-customerName')[0], `${window.location.origin}/api/customers/names`)
}

function populateDepartmentNames() {
    let customer = $('#input-customerName')[0].value
    if (customer === undefined || customer === '') return
    populateSelection($('#input-departmentName')[0], `${window.location.origin}/api/customers/${customer}/departments`)
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
    let data = {
        'customerName': $('#input-customerName')[0].value,
        'departmentName': $('#input-departmentName')[0].value,
        'startTime': $('#input-startTime')[0].value,
        'endTime': $('#input-endTime')[0].value,
        'notes': $('#input-notes')[0].value
    }

    if (data.customerName === '' || data.departmentName === '' || data.startTime === data.endTime) {
        alert("Bitte alle Felder ausfüllen")
        return
    }

    fetch(`${window.location.origin}/api/timetableEntries/create`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (Math.floor(response.status / 100) === 2) {
            window.location.href = `${window.location.origin}/timetable/${data.customerName}/${data.departmentName}/${data.startTime}`
            return
        }
        response.text().then(alert)
    })
}
