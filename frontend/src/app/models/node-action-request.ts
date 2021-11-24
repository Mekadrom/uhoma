import { Action } from '../models';

export interface NodeActionRequest {
  actionWithParams: Action;
  sentEpoch: number;
  toNodeSeq?: number;
  fromNodeSeq?: number;
  toUserSeq?: number;
  fromUserSeq?: number;
}
