import { Room, Action } from '../models';

export interface Node {
  nodeSeq: number;
  name: string;
  room: Room;
  actions: Action[];
}
