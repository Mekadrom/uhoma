import { Home, Room, Action } from '../models';

export interface Node {
  nodeSeq: number;
  name: string;
  room: Room;
  home: Home;
  actions: Action[];
}
