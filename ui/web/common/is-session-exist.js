function isSessionExist(data) {
    $.ajax({
        method: "GET",
        url: SIGNUP_URL,
        data: data,
        success: function (data) {
            if (data !== "") {
                window.location.replace(data);
            }
        }
    })
}