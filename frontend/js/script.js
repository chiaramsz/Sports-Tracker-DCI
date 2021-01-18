let data = new Map();

function receiveDataFromLaptop(trackerId) {




    let list = [trackerId, 60, 400, 10]; //duration, calories, distance

    data.set(1, list);
    data.set(2, list);

}

function visualizeData() {

    trackerId = getTrackerId();

    //duration
    receiveDataFromLaptop(trackerId)

    let duration = document.getElementById("duration");
    let calories = document.getElementById("calories");
    let distance = document.getElementById("distance");

    for(let[key,value] of data) {
        duration.innerText = value[1] + " minutes";
        calories.innerText = value[2] + " kcal";
        distance.innerText = value[3] + " km";
    }
}

visualizeData();


function getTrackerId() {
    let trackerId = document.getElementById("trackerId").value;
    console.log(trackerId);
    return trackerId;
}

function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    zoom: 3,
    center: { lat: 0, lng: -180 },
    mapTypeId: "terrain",
  });
  const flightPlanCoordinates = [
    { lat: 37.772, lng: -122.214 },
    { lat: 21.291, lng: -157.821 },
    { lat: -18.142, lng: 178.431 },
    { lat: -27.467, lng: 153.027 },
  ];
  const flightPath = new google.maps.Polyline({
    path: flightPlanCoordinates,
    geodesic: true,
    strokeColor: "#FF0000",
    strokeOpacity: 1.0,
    strokeWeight: 2,
  });
  flightPath.setMap(map);
}
