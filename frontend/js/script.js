let data = new Map();

function receiveDataFromLaptop() {
    let list = [60, 400, 10]; //duration, calories, distance

    data.set(1, list);
    data.set(2, list);

}




function visualizeData() {

    //duration
    receiveDataFromLaptop()

    let duration = document.getElementById("duration");
    let calories = document.getElementById("calories");
    let distance = document.getElementById("distance");

    for(let[key,value] of data) {
        duration.innerText = value[0] + " minutes";
        calories.innerText = value[1] + " kcal";
        distance.innerText = value[2] + " km";
    }
}

visualizeData();



var po = org.polymaps;

var map = po.map()
    .container(document.getElementById("map").appendChild(po.svg("svg")))
    .add(po.interact())
    .add(po.hash());

map.add(po.image()
    .url(po.url("http://{S}tile.cloudmade.com"
        + "/1a1b06b230af4efdbb989ea99e9841af" // http://cloudmade.com/register
        + "/998/256/{Z}/{X}/{Y}.png")
        .hosts(["a.", "b.", "c.", ""])));

map.add(po.compass()
    .pan("none"));