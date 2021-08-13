import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UrlProviderService {
  private hamsHost: string = 'localhost';
  private hamsPort: string = '8080';

  private webSocketEndpoint = '/app/topic/node';

  constructor() { }

  public getHamsHost(): string {
    return this.hamsHost;
  }

  public setHamsHost(hamsHost: string): void {
    this.hamsHost = hamsHost;
  }

  public getHamsPort(): string {
      return this.hamsPort;
    }

    public setHamsPort(hamsPort: string): void {
      this.hamsPort = hamsPort;
    }

  public getHamsUrl(): string {
    return 'http://' + this.hamsHost + ':' + this.hamsPort;
  }

  public getHamsWebSocketEndpoint(): string {
    return this.webSocketEndpoint;
  }

  public setHamsWebSocketEndpoint(webSocketEndpoint: string): void {
    this.webSocketEndpoint = webSocketEndpoint;
  }

  public getAuthUrl(): string {
    return this.getHamsUrl() + '/auth/login';
  }

  public getNodeSearchUrl(): string {
    return this.getHamsUrl() + '/node/search';
  }

  public getNodeActionParameterTypeSearchUrl(): string {
    return this.getHamsUrl() + '/actionParameter/actionParameterType/search'
  }

  public getNodesSaveUrl(): string {
    return this.getHamsUrl() + '/node/upsertNodes';
  }

  public getRoomSearchUrl(): string {
    return this.getHamsUrl() + '/room/search';
  }
}
