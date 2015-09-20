var marker;
var map;
var geocoder;

function init() {
    $("#login-button").click(function() {
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

     geocoder = new google.maps.Geocoder();
}


function LoginError(msg) {
    $("#login-error").html(msg);
}

function LoginSuccess() {
    $("#login-page").addClass('hide');
}

function UpdateTime(value) {
    $("#date-time").html(value);
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
    var mapCanvas = document.getElementById('map');
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
    $('#car-menu').toggleClass('hide', true)
    $('#health-menu').toggleClass('hide', true)
}

function switchToCar() {
    $('#house-menu').toggleClass('hide', true)
    $('#car-menu').toggleClass('hide', false)
    $('#health-menu').toggleClass('hide', true)
}

function switchToHealth() {
    $('#house-menu').toggleClass('hide', true)
    $('#car-menu').toggleClass('hide', true)
    $('#health-menu').toggleClass('hide', false)
}

function plumberOrderStart() {
    window.JSInterface.checkForService('Hydraulik')
}

function plumberOrderSuccess() {
    $('#order-with-photo').toggleClass('hide', false)
    $('#main-page').toggleClass('hide', true)
}