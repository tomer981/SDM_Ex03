function checkForUpdates() {
    console.log("Checking for updates")
    $.ajax({
        method: "GET",
        url: CHECK_FOR_UPDATES,
        data: {},
        success: function (updates) {
            console.log("~~~~~ Got Updates! ~~~~~")
            console.log(updates)
        }
    })
}