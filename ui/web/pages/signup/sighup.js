import {ThingA, ThingB, ThingC} from 'lib/things';


isSessionExist({action: "isSessionExist",userNotFoundPage: null, userFoundPage: "../zones/zones.html"});
$("#submitForm").ready(function() {
    $(':input[type="submit"]').prop('disabled', true);
    $('input[type="text"]').keyup(function() {
        if($(this).val() !== '') {
            $(':input[type="submit"]').prop('disabled', false);
        }
    });
});