import { Room } from './room';
import { NodeAction } from './node-action';

export interface Node {
  nodeSeq: number;
  name: string;
  room: Room;
  publicActions: NodeAction[];
}
