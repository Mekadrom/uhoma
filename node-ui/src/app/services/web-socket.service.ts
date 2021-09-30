import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Client, Message, Stomp } from '@stomp/stompjs';

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
    this.client = new Client();
    console.log('created new client');
    this.client.configure({
      brokerURL: 'ws://' + this.urlProviderService.getHamsHost() + ':' + this.urlProviderService.getHamsPort() + '/socket',
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
        fromNodeSeq: (userView.node as Node).nodeSeq,
        toNodeSeq: action.ownerNodeSeq,
        actionWithParams: action,
        sentEpoch: millis
      }
      if (reqMsg.toNodeSeq) {
        if (!this.client) {
          this.attach(this.cookieService.get('bearer'), () => this.publish(this.urlProviderService.getHamsWebSocketEndpoint(), reqMsg));
        } else {
          this.publish(this.urlProviderService.getHamsWebSocketEndpoint(), reqMsg);
        }
      }
    }
  }

  private publish(destination: string, message: any): void {
    this.client.publish({destination: destination, body: JSON.stringify(message), skipContentLengthHeader: true});
  }
}
