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

  constructor(private rest: RestService) {
  }

  faMap = faMap;
  faClock = faClock;
  faRunning = faRunning;
  faFire = faFire;
  title = 'sports-tracker';
  trackerId = '';
  latitude = 41.713;
  longitude = -85.003;
  mapType = 'satellite';
  fileData = '';
  duration = '';
  dist = '';
  calories = '';
  coordinates: any = [];
  coordForLine: any = [];
  map: any;

  ngOnInit(): void {

  }

  public getTrackerId(): void {
    this.trackerId = (document.getElementById('trackerId') as HTMLInputElement).value;
    this.visualizeData();
  }

  private visualizeData(): void {
    console.log(this.trackerId);
    this.rest.getTrainingData(this.trackerId)
      .then(r => {
        console.log(r);
        console.log(r.locations);
        this.duration = r.runningTime;
        this.dist = r.distance;
        this.calories = r.kcal;
        this.coordinates = r.locations;

        this.drawPolyline();
      });
  }

  private drawPolyline(): void {
    const texta = (document.getElementById('coordText') as HTMLElement);
    let text = '';
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.coordinates.length; i++){
       text += '{lat: ' + this.coordinates[i][1].latitude + ', lng: ' + this.coordinates[i][0].longitude + '}';
    }
    texta.innerHTML = text;
    this.longitude = this.coordinates[0][0].longitude;
    this.latitude = this.coordinates[0][1].latitude;
    console.log(this.longitude);
    console.log(this.latitude);
    this.map = new google.maps.Map((document.getElementById('map')) as HTMLElement, {
      zoom: 16,
      center: { lat: 0, lng: -180 }
    });


    const trainingPath = new google.maps.Polyline({
      path: this.coordForLine,
      geodesic: true,
      strokeColor: '#FF0000',
      strokeOpacity: 1.0,
      strokeWeight: 4,
    });
    trainingPath.setMap(this.map);
  }
}
