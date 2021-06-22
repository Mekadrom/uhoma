import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UrlProviderService {
  private baseUrl: string = 'http://localhost:8080'; // todo: make this configurable or otherwise not hard coded

  constructor() { }

  public getAuthUrl(): string {
    return this.baseUrl + '/auth/login';
  }

  public getNodeSearchUrl(): string {
    return this.baseUrl + '/node/search';
  }

  public getRoomSearchUrl(): string {
    return this.baseUrl + '/room/search';
  }
}
