import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RestService{
  basicUrl = 'http://';
  port = '8080';
  constructor(private http: HttpClient) { }

  public async getTrainingData(trackerId: string): Promise<any>{
    const host = '34.201.100.51:' + this.port;
    return await this.http.get(this.basicUrl + host + '/' + trackerId)
      .toPromise();
  }
}
