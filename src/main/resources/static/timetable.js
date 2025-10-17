function parameterValue(name) {
    // noinspection JSAssignmentUsedAsCondition
    if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search)) return decodeURIComponent(name[1]);
}

async function init() {
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
    populateUserNames()
}

async function populateCustomerNames() {
    try {
        await populateSelection($('#input-customerName')[0], `${window.location.origin}/api/customers/names`)
    } catch (e) {
        console.warn('Could not populateCustomerNames():', e)
    }
}

async function populateDepartmentNames() {
    try {
        let customer = $('#input-customerName')[0].value
        if (customer === undefined || customer === '') return
        await populateSelection($('#input-departmentName')[0], `${window.location.origin}/api/customers/${customer}/departments`)
    } catch (e) {
        console.warn('Could not populateDepartmentNames():', e)
    }
}

async function populateUserNames() {
    try {
        await populateSelection($('#input-userName')[0], `${window.location.origin}/api/users/names`)
    } catch (e) {
        console.warn('Could not populateUserNames():', e)
    }
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

async function editPropertyAction(url, current) {
    let value = prompt(`Neuer Wert (Aktuell: ${current}):`)
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(value)
    })
    if (response.ok) window.reload()
    else alert(await response.text())
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
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify(data)
    }).then(response => {
        if (Math.floor(response.status / 100) === 2) {
            window.location.href = `${window.location.origin}/timetable/${data.customerName}/${data.departmentName}/${data.startTime}`
            return
        }
        response.text().then(alert)
    })
}

function getUrlEntryInfo() {
    let loc = window.location.href
    let subpath = '/timetable/'
    let start = loc.indexOf(subpath)
    loc = loc.substring(start + subpath.length)
    let obj = {}
    let buf

    obj.customerName = loc.substring(0, buf = loc.indexOf('/'))
    loc = loc.substring(buf + 1)

    obj.departmentName = loc.substring(0, buf = loc.indexOf('/'))
    loc = loc.substring(buf + 1)

    obj.startTime = loc.substring(0, loc.indexOf('/'))

    return obj
}

function submitCreateAssignment() {
    let data = {
        'entryInfo': getUrlEntryInfo(),
        'username': $('#input-userName')[0].value,
        'startTime': $('#input-startTime')[0].value,
        'endTime': $('#input-endTime')[0].value,
        'notes': $('#input-notes')[0].value
    }

    if (data.username === '') {
        alert("Bitte alle Felder ausfüllen")
        return
    }

    fetch(`${window.location.origin}/api/timetableEntries/createAssignment`, {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify(data)
    }).then(response => {
        if (Math.floor(response.status / 100) === 2) {
            let entryInfo = getUrlEntryInfo()
            window.location.href = `${window.location.origin}/timetable/${entryInfo.customerName}/${entryInfo.departmentName}/${entryInfo.startTime}`
            return
        }
        response.text().then(alert)
    })
}
