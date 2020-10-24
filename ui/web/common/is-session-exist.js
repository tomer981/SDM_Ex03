function isSessionExist(data) {
    $.ajax({
        method: "GET",
        url: SIGHUP_URL,
        data: data,
        success: function (data) {
            if (data !== "") {
                window.location.replace(data);
            }
        }
    })
};