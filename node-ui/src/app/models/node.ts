import { NodeAction } from './node-action';
import { Room } from './room';

export interface Node {
  nodeSeq: number;
  name: string;
  room: Room;
  publicActions: NodeAction[];
}
