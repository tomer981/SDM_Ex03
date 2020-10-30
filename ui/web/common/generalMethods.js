// appendToTableView(arrProducts, "#store-product-info")
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

function appendToTableViewInPage(arrObject, tbodyElement) {
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
        tbodyElement.insertRow(output);
        // $(output).appendTo(tbodyElement);
    })
}

//appendToScrollBar(arrStoresInfo, "choose-store", "#store-selector", " -- Select Store -- ", "");
function appendToScrollBar(arrObject, divIdAddTo, idSelector , textSelection , onchangeFunction){
    if (arrObject === "") {
        return
    }
    var output =    '<label for=' + idSelector + '>' + '</label>' +
                    '<select id=' + idSelector +  ' name=' + idSelector +  ' onchange=' + onchangeFunction + '>' +
                        '<option disabled selected value>' + textSelection + '</option>';
    arrObject.forEach((object) => {
        output += '<option value=';
        for (var key in object) {
            if (object.hasOwnProperty(key)) {
                if(key === "value"){
                    output += object[key] + '>';
                }
                else {
                    output += object[key] + '</option>';
                }
            }
        }
    })
    output += '</select>';
    document.getElementById(divIdAddTo).innerHTML +=output;
}


function isFloat(n) {
    number = parseFloat(n)
    return Number(number) === number && number == n;
}

function isInt(n) {
    number = parseInt(n)
    return Number(number) === number && number == n;
}