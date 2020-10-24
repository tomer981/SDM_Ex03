function appendToTableView(arrObject, idAddTo) {
    if (arrObject === "") {
        return
    }
    arrObject.forEach((object) => {
        var output = "<tr>";
        for (var key in object) {
            if (object.hasOwnProperty(key)) {
                output += "<td>" + object[key] + "</td>";
            }
        }
        output += "</tr>";
        $(output).appendTo($(idAddTo));
    })
}