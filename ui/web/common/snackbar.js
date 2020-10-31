function snackbar(elementId, timeout, message) {
    // Get the snackbar DIV
    let element = document.getElementById(elementId);

    let snack = document.createElement('div');
    element.appendChild(snack);

    let $snack = $(snack);

    $snack.addClass("snackbar");
    $snack.addClass("show");
    $snack.html(message);

    // After 3 seconds, remove the show class from DIV
    setTimeout(function () {
        snack.remove();
    }, timeout);
}