import { Home } from '../models';

export interface Room {
  roomSeq?: number;
  home: Home;
  name: string;
}
