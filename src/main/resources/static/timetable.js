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
