import {Component, OnInit} from '@angular/core';
import {faClock, faFire, faRunning, faMap} from '@fortawesome/free-solid-svg-icons';
import {RestService} from './rest-service.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  styles: ['agm-map { height: 300px; /* height is required */ }'],
})

export class AppComponent implements OnInit {

  constructor(private rest: RestService) {}

  faMap = faMap;
  faClock = faClock;
  faRunning = faRunning;
  faFire = faFire;
  title = 'sports-tracker';
  latitude = -28.68352;
  longitude = -147.20785;
  mapType = 'satellite';
  fileData = '';
  duration = '';
  distance = '';
  calories = '';
  map: any;

  ngOnInit(): void {
    this.visualizeData();

  }



  private visualizeData(): void {
    this.rest.getTrainingData()
      .then(r => console.log(r));
    this.distance = '3km';
    this.calories = '200kcal';
    this.duration = '30min';
  }



  private readFile(): void {
  }


  private getJson(): string {
    return this.fileData;
  }


  private getTrackerId(): void {
    // @ts-ignore
    const trackerId = document.getElementById('trackerId').innerText;
    const file = trackerId + '.txt';
    console.log('File ' + file);
    console.log(trackerId);


  }

  /*
  private initMap(): void {
    this.map = new google.maps.Map(document.getElementById('map'), {
      center: {lat: 41.713, lng: -85.003},
      zoom: 16
    });

    const flightPlanCoordinates = [
      {lat: 41.7171899900261, lng: -85.002969973285587},
      {lat: 41.716339720601695, lng: -85.00356011920411},
      {lat: 41.715420123340095, lng: -85.003969783778473},
      {lat: 41.713850219112373, lng: -85.0043800221203},
      {lat: 41.709869880890324, lng: -85.004809740676933},
      {lat: 41.709570224086633, lng: -85.004860160268152},

    ];

    const flightPlanCoordinates2 = [
      {lat: 42, lng: -86},
      {lat: 42, lng: -87},
      {lat: 42, lng: -88},
      {lat: 43, lng: -88},
      {lat: 44, lng: -89},
      {lat: 49, lng: -89},

    ];

    const arrayOfFlightPlans = [flightPlanCoordinates, flightPlanCoordinates2];

    // Loops through all polyline paths and draws each on the map.
    for (let i = 0; i < 2; i++) {
      const flightPath = new google.maps.Polyline({
        path: arrayOfFlightPlans[i],
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 4,
      });

      flightPath.setMap(map);
    }

  }
   */
}
