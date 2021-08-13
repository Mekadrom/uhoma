import { Room } from '../models';

export interface RoomLink {
  roomLinkSeq: number;
  startRoom: Room;
  endRoom: Room;
  transitionLocationDef: string;
}
