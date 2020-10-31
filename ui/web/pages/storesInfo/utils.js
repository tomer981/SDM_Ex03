
function getProductsInZone(zoneName, cb) {
    $.ajax({
        method: "Get",
        url: STORES_URL,
        data: {action: "getProductsInZone", zoneName: zoneName},
        success: cb,
    })
}
