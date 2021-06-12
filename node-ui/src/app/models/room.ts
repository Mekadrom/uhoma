import { RoomLink } from './room-link';

export interface Room {
  id: number;
  name: string;
  room_links: RoomLink[];
}
