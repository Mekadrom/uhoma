import { NodeAction } from './node-action';
import { Room } from './room';

export interface Node {
  id: number;
  name: string;
  room: Room;
  node_actions: NodeAction[];
}
