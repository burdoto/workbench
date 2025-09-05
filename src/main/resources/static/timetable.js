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
}

function refreshTableView(user = null, year = null, week = null, customer = null) {
    let displayInfoText, display

    if (week !== null) {
        displayInfoText = "Kalenderwoche: " + week + " in " + year
        display = entries.findWeekByUser(user, year, week);
    } else if (customer != null) {
        displayInfoText = "Kunde: %s".formatted(customer);
        display = 'http://localhost:8080/api/timetableEntries/search/findByCustomerNameOrderByStartTimeAsc?customerName=' + customer;
    } else {
        displayInfoText = "Aktuelle Kalenderwoche";
        display = entries.findThisWeekByUser(user);
    }
}

function addEntryRow() {
}

function createEntry() {
    let form = $('#panel-create-entry form').first()

    fetch()
}
