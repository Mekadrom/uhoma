import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Client, Message, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

import { CookieService } from 'ngx-cookie-service';

import { Action, Node, NodeActionRequest, UserView } from '../models';
import { AuthService, UrlProviderService, UserProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  client?: any;

  constructor(private authService: AuthService,
              private cookieService: CookieService,
              private urlProviderService: UrlProviderService,
              private userProviderService: UserProviderService) {}

  public isConnected(): boolean {
    return !!this.client;
  }

  attach(jwt: string | null | undefined, connectCallback?: () => void): void {
    this.client = Stomp.over(new SockJS(this.urlProviderService.getHamsWebSocketUrl()));
    console.log('created new client');
    console.log('attaching socket on: ' + this.urlProviderService.getHamsWebSocketUrl());
    this.client.configure({
//       brokerURL: 'ws://' + this.urlProviderService.getHamsHost() + ':' + this.urlProviderService.getHamsPort() + '/socket',
//       brokerURL: this.urlProviderService.getHamsWebSocketUrl(),
      connectHeaders: {
        Authorization: 'Bearer ' + jwt
      },
      onConnect: () => {
        if (connectCallback) {
          connectCallback();
        }
      },
      debug: (str: string) => {
        console.log(new Date(), str);
      }
    });
    console.log('configured client');
    this.client.activate();
    console.log('activated client');
  }

  executeAction(action: Action): void {
    let userView: any = this.userProviderService.getUserView();
    if (userView) {
      this.doExecuteAction(action, userView);
    } else {
      const that: any = this;
      this.authService.refreshJwt(this.cookieService.get('refreshToken')).subscribe(
        (resp: HttpResponse<UserView>) => {
          that.doExecuteAction(action, resp.body);
        }
      );
    }
  }

  private doExecuteAction(action: Action, userView: UserView): void {
    console.log('sending: ' + JSON.stringify(action));
    if (action && userView) {
      const millis = Math.round((new Date()).getTime());
      const reqMsg = {
        fromNodeSeq: userView.node ? (userView.node as Node).nodeSeq : null,
        toNodeSeq: action.ownerNodeSeq,
        actionWithParams: action,
        sentEpoch: millis,
        username: userView.username
      }
      if (reqMsg.toNodeSeq) {
        const jwt: string = this.cookieService.get('bearer');
        if (!this.client || !this.client.connected) {
          this.attach(jwt, () => this.publish(this.urlProviderService.getHamsWebSocketMessageEndpoint(), jwt, reqMsg));
        } else {
          this.publish(this.urlProviderService.getHamsWebSocketMessageEndpoint(), jwt, reqMsg);
        }
      }
    }
  }

  private publish(destination: string, jwt: string, message: any): void {
    this.client.publish({destination: destination, headers: {Authorization: 'Bearer ' + jwt}, body: JSON.stringify(message), skipContentLengthHeader: true});
  }
}
