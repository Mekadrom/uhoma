import { Room } from './room';

export interface RoomLink {
  roomLinkSeq: number;
  startRoom: Room;
  endRoom: Room;
  transitionLocationDef: string;
}
