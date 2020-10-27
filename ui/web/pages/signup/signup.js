isSessionExist({action: "isSessionExist",userNotFoundPage: null, userFoundPage: "../zones/zones.html"});

function submitButtonValidateParameters() {
    $("#submitFormButton").ready(function () {
        if ($("#user-name").val() !== "") {
            if ($("#user-position").find("option:selected").attr("value") === "customer") {
                $("#add-file").prop('disabled', true);
                $("#add-file").val("");
                $("#submitFormButton").prop("disabled", false);
            } else if ($("#user-position").find("option:selected").attr("value") === "manager") {
                $("#add-file").prop('disabled', false);
                if ($("#add-file").val() !== "") {
                    $("#submitFormButton").prop("disabled", false);
                } else {
                    $("#submitFormButton").prop("disabled", true);
                }
            }
        } else {
            $("#add-file").prop('disabled', true);
            $("#add-file").val("");
            $("#submitFormButton").prop("disabled", true);
        }
    })
}
//
// $("#submit-form").on("submit", function (e) {
//     $.ajax({
//         url: SIGNUP_URL,
//         type: this.method,
//         data: new FormData(this),
//         processData: false,
//         contentType: false,
//         cache: false,
//         dataType: "html",
//         success: function (nextUrl) {
//             window.location.replace(nextUrl);
//         },
//         error: function (msg) {
//             var output = $("<p id='error' style='color: #ff0000'></p>").text(msg.responseText);
//             $("#error").remove();
//             $("body").append(output)
//         }
//     });
//
//     return false;
// });
