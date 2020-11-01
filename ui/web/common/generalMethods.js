// appendToTableView(arrProducts, "#store-product-info")
function appendToTableView(arrObject, idAddTo) {
    if (arrObject === "") {
        return
    }
    arrObject.forEach((object) => {
        var output = "<tr>";
        for (var key in object) {
            if (object.hasOwnProperty(key)) {
                if (typeof object[key] == "number"){
                    object[key] = toTwoDigit(object[key]);
                }
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
        let row = tbodyElement.insertRow();
        for (let key in object) {
            if (object.hasOwnProperty(key)) {
                let cell = row.insertCell()
                if (typeof object[key] == "number"){
                    object[key] = toTwoDigit(object[key]);
                }
                cell.innerHTML = object[key];
            }
        }
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

function formToJson($form){
    const array = $form.serializeArray();
    let indexed_array = {};

    $.map(array, function(n, i) {
        const names = n['name'].split('.');

        // This can be done in a more sophisticated way to take into account objects and arrays but we need only
        // arrays so this is the simple solution
        if (names.length === 1) {
            indexed_array[names[0]] = n['value'];
        } else {
            if (indexed_array[names[0]] === undefined) {
                indexed_array[names[0]] = [];
            }

            while (indexed_array[names[0]].length <= parseInt(names[1])) {
                indexed_array[names[0]].push({});
            }

            indexed_array[names[0]][names[1]][names[2]] = n['value'];
        }
    });

    return indexed_array;
}

function sendJsonForm(form, url, successFn, errorFn) {
    let jsonData = formToJson($(form));

    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(jsonData),
        contentType: 'application/json; charset=utf-8',
        processData: false,
        cache: false,
        dataType: "json",
        complete: (resp) => {
            const data = resp.responseJSON;
            if (data !== undefined && data.error !== undefined) {
                errorFn(data.error)
            } else {
                successFn(data)
            }
        }
    });
}

function  toTwoDigit(x){
    if (isInt(x)){
        return x;
    }

    return Number.parseFloat(x).toFixed(2);
}

function resizeIframe(obj) {
    obj.style.height = obj.contentWindow.document.documentElement.scrollHeight + 'px';
}