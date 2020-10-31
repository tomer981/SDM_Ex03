const interval = 5000
let updatesCheckerHandler = null;
let isChecking = false

function startCheckForUpdates(cb) {
    if (updatesCheckerHandler === null) {
        updatesCheckerHandler = setInterval(() => {
            if (isChecking) return
            isChecking = true
            $.ajax({
                method: "Get",
                url: CHECK_FOR_UPDATES,
                data: {},
                success: function (updatesString) {
                    let updates = null
                    try {
                        updates = JSON.parse(updatesString)
                        cb(updates)
                        isChecking = false

                        if (updates.stopChecking !== undefined && updates.stopChecking) {
                            clearInterval(updatesCheckerHandler)
                            updatesCheckerHandler = null;
                        }
                    } catch(err) {
                        console.log("Could not parse updates")
                        console.log(err)

                        isChecking = false
                    }

                }
            })
        }, interval);
    }
}