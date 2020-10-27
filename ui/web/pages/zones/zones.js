isSessionExist({action: "isSessionExist", userNotFoundPage: "../../index.html", userFoundPage: ""});
$("#userslist").empty();
$("#user-transaction-tableview").empty();
$("#zone-info-tableview").empty();



function submitButtonValidateParameters() {
    if ($("#date").val() !== "" && $("#deposit-amount").val() !== "") {
        if (isFloat($("#deposit-amount").val())) {
            $("#submit-deposit").prop("disabled", false)
        } else {
            $("#submit-deposit").prop("disabled", true)
        }
    } else {
        $("#submit-deposit").prop("disabled", true)
    }
}

function createMakeTransactionLayout() {
    var output = "<form action='ZonesServlet' method='POST' id='submitForm' enctype='multipart/form-data'>";
    output += '<br><input type="date"  name="date" id="date" placeholder="Pick a date" autocomplete="off" onchange="submitButtonValidateParameters()">     ';
    output += '<input type="text" id="deposit-amount" name="depositAmount" placeholder="Amount Deposit" autocomplete="off" onkeyup="submitButtonValidateParameters()">       ';
    output += '<input type="submit" value="Add Deposit" class="button" disabled="disabled" id="submit-deposit">';
    $(output).appendTo($("#make-deposit"));

    $("#submitForm").on("submit", function (e) {
        $.ajax({
            url: ZONES_URL,
            type: this.method,
            data: $(this).serialize(),
            success: function () {
                window.location.reload();
            },
        });

        return false;
    });
}

function moveToZone(zoneName){
    var url = "../storesInfo/storesInfo.html?zoneName=" + zoneName;
    window.location.replace(url);
}

$.ajax({
    method: "GET",
    url: SIGNUP_URL,
    data: {action: "isCustomer"},
    success: function (isCustomer) {
        if (isCustomer === "true") {
            createMakeTransactionLayout();
        }
    }
})

$.ajax({
    method: "Get",
    url: ZONES_URL,
    data: {action: "getKUsersVPosition"},
    success: function (KUsersVPosition) {
        appendToTableView(KUsersVPosition, "#users-tableview");
    }
})

$.ajax({
    method: "GET",
    url: ZONES_URL,
    data: {action: "getUserTransactions"},
    success: function (transactions) {
        appendToTableView(transactions, "#user-transaction-tableview");
    },
})




$.ajax({
    method: "Get",
    url: ZONES_URL,
    data: {action: "getZoneInfo"},
    success: function (zones) {
        appendToTableView(zones, "#zone-info-tableview")
        $("#zone-table tbody tr").click(function () {
            $(this).addClass("selected").siblings().removeClass("selected");
            var zoneName = $(this).find("td:first").html();
            moveToZone(zoneName);

        });
    },
})