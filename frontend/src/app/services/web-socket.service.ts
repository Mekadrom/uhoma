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

  attach(jwt: string | null | undefined, connectCallback?: () => void): void {
    this.client = Stomp.over(new SockJS(this.urlProviderService.getHamsWebSocketUrl()));
    this.client.configure({
      connectHeaders: {
        'Authorization': 'Bearer ' + jwt
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
    this.client.activate();
  }

  executeAction(action: Action, lastChance: boolean): void {
    const userView: any = this.userProviderService.getUserView();
    const jwt: string | null | undefined = this.cookieService.get('bearer');
    if (userView) {
      this.doExecuteAction(action, userView);
    }
  }

  private doExecuteAction(action: Action, userView: UserView): void {
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
    this.client.publish({destination: destination, headers: {'Authorization': 'Bearer ' + jwt}, body: JSON.stringify(message), skipContentLengthHeader: true});
  }

  public isConnected(): boolean {
    return this.client && this.client.connected;
  }
}
