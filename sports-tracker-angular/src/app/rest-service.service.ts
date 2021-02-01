import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DataResponse} from './DataResponse';

@Injectable({
  providedIn: 'root'
})
export class RestService{
  basicUrl = 'http://';
  constructor(private http: HttpClient) { }

  public getTrainingData(trackerId: string): Promise<DataResponse>{
    const host = 'ec2-54-235-229-86.compute-1.amazonaws.com';
    return this.http.get(this.basicUrl + host + '/' + trackerId)
      .toPromise()
        .then(item => (item as {items: DataResponse}).items);
  }
}
