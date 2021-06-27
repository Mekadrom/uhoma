import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UrlProviderService {
  private hamsUrl: string = 'http://localhost:8080'; // todo: make this configurable or otherwise not hard coded

  constructor() { }

  public getHamsUrl(): string {
    return this.hamsUrl;
  }

  public setHamsUrl(hamsUrl: string): void {
    this.hamsUrl = hamsUrl;
  }

  public getAuthUrl(): string {
    return this.hamsUrl + '/auth/login';
  }

  public getNodeSearchUrl(): string {
    return this.hamsUrl + '/node/search';
  }

  public getRoomSearchUrl(): string {
    return this.hamsUrl + '/room/search';
  }
}
