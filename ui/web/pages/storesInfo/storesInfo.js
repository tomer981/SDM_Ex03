isSessionExist({action: "isSessionExist", userNotFoundPage: "../../index.html", userFoundPage: ""});
$("#zone-products-tableview").empty();
$("#general-store-info-tableview").empty();
$("#store-product-info").empty();

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const zoneName = urlParams.get('zoneName')

$.ajax({
    method: "Get",
    url: STORES_URL,
    data: {action: "getProductsInZone", zoneName: zoneName},
    success: function (arrProducts) {
        appendToTableView(arrProducts, "#zone-products-tableview");
    },
})

$.ajax({
    method: "Get",
    url: STORES_URL,
    data: {action: "getStoreInfo", zoneName: zoneName},
    success: function (arrStoreInfo) {
        appendToTableView(arrStoreInfo, "#general-store-info-tableview");
        $("#stores-table tbody tr").click(function () {
                $(this).addClass("selected").siblings().removeClass("selected");
                var storeId = $(this).find("td:first").html();
                getStoreProducts(storeId);
            }
        )
    },
    error: function () {
        alert("error getStoreInfo");
    }
})

function getStoreProducts(storeId) {
    $.ajax({
        method: "Get",
        url: STORES_URL,
        data: {action: "getStoreProductsInfo", zoneName: zoneName, storeId: storeId},
        success: function (arrProducts) {
            $("#store-product-info").empty();
            appendToTableView(arrProducts, "#store-product-info")
        },
        error: function () {
            alert("error getStoreProductsInfo");
        }
    })
}

function activateSubmitButton(){
    $("#submitFormButton").prop("disabled",false);
}

function addCustomerAction(){
    var selection =
        '<form action="StoresServlet" method="POST" id="submit-form">' +
            '<label for="action-selected">Choose an Action:</label>' +
            '<select name="action" id="action-selected" onchange="activateSubmitButton()">' +
                '<option style="display: none"></option>' +
                '<option value="newOrder"> New Order</option>' +
                '<option value="showOrder"> Show Order</option>' +
            '</select>' +
            '<input type="submit" value="Activate" class="button" disabled="disabled" id="submitFormButton"/>' +
        '</form>';
    document.getElementById("user-action-container").innerHTML +=selection;

    var input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "zoneName")
        .attr("value", zoneName);

    $('#submit-form').append(input);
}

function addManagerAction(){
    var selection =
        '<form action="StoresServlet" method="POST" id="submit-form">' +
            '<label for="action-selected">Choose an Action:</label>' +
            '<select name="action" id="action-selected" onchange="activateSubmitButton()">\n' +
                '<option style="display: none"></option>' +
                '<option value="showOrdersManager"> Show Order</option>' +
                '<option value="showFeedbacks"> Show Feedbacks</option>' +
                '<option value="newStore"> New Store</option>' +
            '</select>' +
            '<input type="submit" value="Activate" class="button" disabled="disabled" id="submitFormButton"/>' +
        '</form>';

    document.getElementById("user-action-container").innerHTML +=selection;
    var input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "zoneName")
        .attr("value", zoneName);

    $('#submit-form').append(input);
}

$.ajax({
    method: "GET",
    url: SIGNUP_URL,
    data: {action: "isCustomer"},
    success: function (isCustomer) {
        if (isCustomer === "true") {
            addCustomerAction();
        } else {
            addManagerAction()
        }
    }
})

