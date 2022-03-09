import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlProviderService {
  private webSocketEndpoint: string = '/socket';
  private webSocketMessageEndpoint = '/app/nodeaction';

  constructor() { }

  public getHamsUrl(): string {
    return 'http://' + environment.apiUrl + ':' + environment.apiPort;
  }

  public getHamsWebSocketUrl(): string {
    return 'http://' + environment.apiUrl + ':' + environment.apiPort + this.webSocketEndpoint;
  }

  public getHamsWebSocketMessageEndpoint(): string {
    return this.webSocketMessageEndpoint;
  }

  public getLoginUrl(): string {
    return this.getHamsUrl() + '/auth/login';
  }

  public getRegistrationUrl(): string {
    return this.getHamsUrl() + '/auth/register';
  }

  public getUserViewRefreshUrl(): string {
    return this.getHamsUrl() + '/auth/refreshUserView';
  }

  public getHomeSearchUrl(): string {
    return this.getHamsUrl() + '/home/search';
  }

  public getHomeCreateUrl(): string {
    return this.getHamsUrl() + '/home/upsert';
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
