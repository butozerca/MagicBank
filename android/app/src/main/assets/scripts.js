 function init() {
    /*$("#dupap").html("duppppppaaa");

    $('#lines').on('change', function() {
        window.JSInterface.showStopsForLine(this.value);
    });

    $('#stops').on('change', function() {
        window.JSInterface.showLinesForStop(this.value);
    });*/
}


function FillUserInfo(id, name, surname, email) {
    $("#userInfo").html(id + "<br>" + name + "<br>" + surname + "<br>" + email);
}

function appendOperation(name, description, price) {
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
    $("#userOperations").html("<img src='" + img + "' />");
}

var marker;
var map;
var geocoder;

function initMap(lat, lng) {
    geocoder = new google.maps.Geocoder();
     var mapCanvas = document.getElementById('map');
     var mapOptions = {
           center: new google.maps.LatLng(lat, lng),
           zoom: 14,
           mapTypeId: google.maps.MapTypeId.ROADMAP
     }
     map = new google.maps.Map(mapCanvas, mapOptions);

     marker = new google.maps.Marker({
         position: {lat, lng},
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
        marker = new google.maps.Marker({
            map: map,
            position: results[0].geometry.location
        });
      } else {
        alert("Geocode was not successful for the following reason: " + status);
      }
    });
}
