import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RestService{

  constructor(private http: HttpClient) { }

  public getTrainingData(): Promise<void>{
    return this.http.get('')
      .toPromise()
      .then();
  }
}
