import { Injectable } from '@angular/core';

import { UrlProviderService } from '../services/url-provider.service';
import { UserProviderService } from '../services/user-provider.service';

import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';
import { UserView } from '../models/user-view';
import { NodeActionRequest } from '../models/node-action-request';

import { Client, Message } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  client?: any;

  constructor(private urlProvider: UrlProviderService,
              private userProvider: UserProviderService) { }

  sendAction(nodeAction?: NodeAction): void {
  const userView: any = this.userProvider.getUserView();
    if (nodeAction && userView) {
      const millis = Math.round((new Date()).getTime());
      const reqMsg = {
        fromNodeSeq: (userView.node as any).nodeSeq,
        toNodeSeq: nodeAction.ownerNode.nodeSeq,
        nodeActionWithActionParams: nodeAction,
        sentEpoch: millis
      }
      if (reqMsg.toNodeSeq) {
        this.client?.publish({
          destination: this.urlProvider.getHamsWebSocketEndpoint(),
          body: reqMsg,
          skipContentLengthHeader: true
        });
      }
    }
  }

  attach(jwt: string | null | undefined): void {
    this.client = new Client({
      brokerURL: 'ws://' + this.urlProvider.getHamsHost() + '/ws',
      connectHeaders: {
        Authorization: 'Bearer: ' + jwt
      },
      debug: function (str: string) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.init(this.client);
  }

  init(client: any): void {
    client.onConnect = function (frame: any) {
      client.subscribe('/user/topic/node');
    };

    client.onStompError = function (frame: any) {
      console.log('Broker reported error: ' + frame.headers['message']);
      console.log('Additional details: ' + frame.body);
    };

    client.activate();
  }
}
