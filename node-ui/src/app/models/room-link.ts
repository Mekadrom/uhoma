import { Room } from './room';

export interface RoomLink {
  id: number;
  start_room: Room;
  end_room: Room;
  transition_def: string;
}
