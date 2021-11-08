import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlProviderService {
  //private webSocketEndpoint = '/app/send/message';
  private webSocketEndpoint: string = '/socket';

  constructor() { }

  public getHamsUrl(): string {
    return 'http://' + environment.apiUrl + ':' + environment.apiPort;
  }

  public getHamsWebSocketUrl(): string {
    return 'ws://' + environment.apiUrl + ':' + environment.apiPort + this.webSocketEndpoint;
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

  public getTokenRefreshUrl(): string {
    return this.getHamsUrl() + '/auth/refreshToken';
  }

  public getUserViewRefreshUrl(): string {
    return this.getHamsUrl() + '/auth/refreshUserView';
  }

  public getNodeSearchUrl(): string {
    return this.getHamsUrl() + '/node/search';
  }

  public getNodeActionParameterTypeSearchUrl(): string {
    return this.getHamsUrl() + '/actionParameterType/search'
  }

  public getActionHandlerSearchUrl(): string {
    return this.getHamsUrl() + '/actionHandler/search'
  }

  public getNodesSaveUrl(): string {
    return this.getHamsUrl() + '/node/upsertNodes';
  }

  public getRoomSearchUrl(): string {
    return this.getHamsUrl() + '/room/search';
  }
}
