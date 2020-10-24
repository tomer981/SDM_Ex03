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

