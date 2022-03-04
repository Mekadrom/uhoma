import { Node } from '../models';

export interface UserView {
  username: string;
  userLoginSeq: number;
  node?: Node;
}
