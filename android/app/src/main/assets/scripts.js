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
            Login(login, pass);
        }
    });
}

function Login(login, pass) {
    window.JSInterface.login(login + ";" + pass);
}

function LoginError(msg) {
    $("#login-error").html(msg);
}

function LoginSuccess() {
    $("#login-page").addClass('hide');
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
    $("#img").html("<img src='" + img + "' />");
}

var marker;
var map;
var geocoder;

function initMap(lat, lng) {
    var mapCanvas = document.getElementById('map');
    var mapOptions = {
          center: new google.maps.LatLng(lat, lng),
          zoom: 14,
          mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(mapCanvas, mapOptions);
    geocoder = new google.maps.Geocoder();
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
            document.getElementById("address").value =  results[0].formatted_address;
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
