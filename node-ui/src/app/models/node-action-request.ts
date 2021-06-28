import { NodeAction } from './node-action';

export interface NodeActionRequest {
  fromNodeSeq: number;
  toNodeSeq: number;
  nodeActionWithParams: NodeAction;
  sentEpoch: number;
}
