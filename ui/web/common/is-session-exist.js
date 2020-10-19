function isSessionExist(data) {
    $.ajax({
        method: "POST",
        url: SIGHUP,
        data: data,
        success: function (data) {
            if (data !== "") {
                window.location.replace(data);
            }
        }
    })
};