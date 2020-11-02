
function snackbar_init(elementId) {
    if (document.getElementById(elementId) !== null) return;

    let snack = document.createElement('div');
    snack.setAttribute('id', elementId);

    document.body.appendChild(snack);
}

function snackbar_show(elementId, timeout, message) {
    // Get the snackbar DIV
    let element = document.getElementById(elementId);

    let snack = document.createElement('div');
    element.appendChild(snack);

    let $snack = $(snack);

    $snack.addClass("snackbar");
    $snack.addClass("show");
    $snack.html(message);

    setTimeout(function () {
        snack.remove();
    }, timeout);
}