var marker;
var map;
var geocoder;

var timerInterval;
var timerValue;

function init() {
    $("#login-button").click(function() {
        console.log("dupa");

        var login = $("#login-username").val();
        var pass = $("#login-password").val();
        if(login == null || pass == null) {
            LoginError("Nie mozna odczytac danych logowania");
        }
        else if (login.length == 0 || pass.length == 0) {
            LoginError("Pola login i haslo nie moga byc puste!");
        }
        else {
            window.JSInterface.login(login + ";" + pass);
        }
    });

    $("#take-photo").click(function() {
        takePhoto();
    });

    $("#date-picker").click(function() {
        window.JSInterface.GetDateTime();
    });

    $("#use-location").click(function() {
        window.JSInterface.UseCurrentLocation();
    });

    $("#commit").click(function() {
        $("#popup-alert").removeClass('hide');
    });

     $("#popup-alert-cancel").click(function() {
        $("#popup-alert").addClass('hide');
     });

     $("#popup-alert-ok").click(function() {
        timerValue = calculateTimerValue() * 60 + 1;
        timer();

        $("#popup-alert").addClass('hide');
        $("#order-with-photo").addClass('hide');
        $("#order-confirmation").removeClass('hide');

     });

     $("#plumber-close").click(function() {
        $("#order-with-photo").addClass('hide');
        $("#main-page").removeClass('hide');
     });

     $("#confirmation-ok").click(function() {
        $("#order-confirmation").addClass('hide');
         $("#main-page").removeClass('hide');
     });

     $("#order-with-map-close").click(function() {
         $("#order-with-map").addClass('hide');
         $("#main-page").removeClass('hide');
      });

    geocoder = new google.maps.Geocoder();

    timerValue = 10*60;
    timerInterval = setInterval(function () { timer() }, 1000);
}

function calculateTimerValue() {
    var oldDate = $("#date").html();
    var dateString = oldDate[3] + oldDate[4] + "/" + oldDate[0] + oldDate[1] + "/" + oldDate[6] + oldDate[7] + oldDate[8] + oldDate[9];
    dateString += " " + $("#time").html() + ":00";

    console.log(dateString);
    var date = Date.parse(dateString);
    console.log(date);
    var now = Date.now();
    console.log(now);
    var diff = Math.abs(date - now);
    console.log(diff);
    var minutes = Math.floor((diff/1000)/60);
    console.log(minutes);
    if(minutes < 60)
        minutes = 60;

    return minutes;
}

function timer() {
    timerValue -= 1;

    if(timerValue <= 0) {
        timerValue = 0;
        window.clearInterval(timerInterval);
    }

    var hours = parseInt(timerValue / 3600);
    if(hours < 10)
            hours = "0" + hours;

    var minutes = parseInt((timerValue % 3600) / 60);
    if(minutes < 10)
        minutes = "0" + minutes;

    var seconds = timerValue % 60;
    if(seconds < 10)
        seconds = "0" + seconds;
    $("#timer-text-clock").html(hours + ":" + minutes + ":" + seconds);
}

function LoginError(msg) {
    $("#login-error").html(msg);
}

function LoginSuccess() {
    window.JSInterface.userLoggedIn();
    $("#login-page").addClass('hide');
    $("#main-page").removeClass('hide');
}

function fillLoggedUserName(name) {
    $("#logged-user").html(name)
}


function UpdateTime(value) {
    console.log(value);
    $("#date-picker").html("<span>Realizacja:</span>" + value);
    console.log("dupa");
}

function SetLocationCoords(lat, lng) {
    $("#address").val(lat + ", " + lng);

    geocoder.geocode( { 'location': new google.maps.LatLng(lat, lng) }, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            $("#address").val(results[0].formatted_address);
        } else {
            console.log("Reverse geocode was not successful for the following reason: " + status);
        }
    });
}

function SetLocationCoords2(lat, lng) {
    $("#address-bar-map-order").val(lat + ", " + lng);

    geocoder.geocode( { 'location': new google.maps.LatLng(lat, lng) }, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            $("#address-bar-map-order").val(results[0].formatted_address);
        } else {
            console.log("Reverse geocode was not successful for the following reason: " + status);
        }
    });
}

function FillUserInfo(id, name, surname, email) {
    $("#userInfo").html(id + "<br>" + name + "<br>" + surname + "<br>" + email);
}

function appendService(name, description, price) {
    if(price == -1)
        $("#userOperations").append(name + ":<br>" + description + "<br><br>");
    else
        $("#userOperations").append(name + " (" + price + " zł):<br>" + description + "<br><br>");
}

function takePhoto() {
    window.JSInterface.takePhoto();
}

function readLocation() {
    window.JSInterface.readLocation();
}

function SetPicture(img) {
    $("#take-photo").html("<img src='" + img + "' />");
}

function initMap(lat, lng) {
    var mapCanvas = document.getElementById('google-map');
    var mapOptions = {
          center: new google.maps.LatLng(lat, lng),
          zoom: 14,
          mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(mapCanvas, mapOptions);

    marker = new google.maps.Marker({
        position: new google.maps.LatLng(lat, lng),
        map: map,
        title: 'Tu jestem!',
        draggable: true
    });

    SetLocationCoords2(marker.getPosition().lat(), marker.getPosition().lng())
    google.maps.event.addListener(marker, 'dragend', function() {
        SetLocationCoords2(marker.getPosition().lat(), marker.getPosition().lng())
    });
 }

function readMarkerLocation() {
    window.JSInterface.readMarkerLocation(marker.getPosition().lat() + " " + marker.getPosition().lng());
}

function ShowError(msg) {
    $("#error").html("Error: " + msg);
}

function markerToAddress() {
    var location = marker.getPosition();
    geocoder.geocode( { 'location': location}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            document.getElementById("address").value = results[0].formatted_address;
        } else {
            alert("Reverse geocode was not successful for the following reason: " + status);
        }
    });
}

function addressToMarker() {
    var address = document.getElementById("address").value;
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
        map.setCenter(results[0].geometry.location);
        marker.setPosition(results[0].geometry.location);
      } else {
        alert("Geocode was not successful for the following reason: " + status);
      }
    });
}

function switchToHouse() {
    $('#house-menu').toggleClass('hide', false)
    $('#home').toggleClass('hideafter', false)
    $('#car-menu').toggleClass('hide', true)
    $('#car').toggleClass('hideafter', true)
    $('#health-menu').toggleClass('hide', true)
    $('#health').toggleClass('hideafter', true)
}

function switchToCar() {
    $('#house-menu').toggleClass('hide', true)
    $('#home').toggleClass('hideafter', true)
    $('#car-menu').toggleClass('hide', false)
    $('#car').toggleClass('hideafter', false)
    $('#health-menu').toggleClass('hide', true)
    $('#health').toggleClass('hideafter', true)
}

function switchToHealth() {
    $('#house-menu').toggleClass('hide', true)
    $('#home').toggleClass('hideafter', true)
    $('#car-menu').toggleClass('hide', true)
    $('#car').toggleClass('hideafter', true)
    $('#health-menu').toggleClass('hide', false)
    $('#health').toggleClass('hideafter', false)
}

function plumberOrderStart() {
    $('#order-with-photo').toggleClass('hide', false)
    $('#main-page').toggleClass('hide', true)
}

function plumberAfterFormCompleted() {
    window.JSInterface.checkForService('Hydraulik')
}

function plumberOrderSuccess() {
// TODO
}

function popupServiceCost(serviceTuple) {
    var split = serviceTuple.split(":")
    var svcid = split[0]
    var svcname = split[1]
    var svcprice = split[2]
    //TODO
}

function popupCantFindSuitableService(serviceName) {
    //TODO
}

function refuelOrderStart() {
    setupMapOrder()
    $('#order-with-map').toggleClass('hide', false)
    $('#main-page').toggleClass('hide', true)
}

function refuelAfterFormCompleted() {
    window.JSInterface.checkForService('Dowóz paliwa')
}

function setupMapOrder() {
    readLocation()
}