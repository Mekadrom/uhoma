import { Action } from '../models';

export interface NodeActionRequest {
  fromNodeSeq?: number;
  toNodeSeq: number;
  actionWithParams: Action;
  sentEpoch: number;
  username?: string;
}
