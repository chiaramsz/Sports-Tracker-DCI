import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DataResponse} from './DataResponse';

@Injectable({
  providedIn: 'root'
})
export class RestService{
  basicUrl = 'http://';
  constructor(private http: HttpClient) { }

  public getTrainingData(): Promise<DataResponse>{
    const host = '';
    return this.http.get(this.basicUrl + host)
      .toPromise()
        .then(item => (item as {items: DataResponse}).items);
  }
}
