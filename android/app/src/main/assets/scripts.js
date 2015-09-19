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

function initMap(lat, lng) {
    var mapCanvas = document.getElementById('map');
    var mapOptions = {
          center: new google.maps.LatLng(lat, lng),
          zoom: 14,
          mapTypeId: google.maps.MapTypeId.ROADMAP
    }
    var map = new google.maps.Map(mapCanvas, mapOptions);

    var marker = new google.maps.Marker({
        position: {lat, lng},
        map: map,
        title: 'Tu jestem!'
    });
}
