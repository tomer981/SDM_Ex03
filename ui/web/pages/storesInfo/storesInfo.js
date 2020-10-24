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

