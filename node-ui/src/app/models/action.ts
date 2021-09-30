import { Node, ActionHandler, ActionParameter } from '../models';

export interface Action {
  actionSeq?: number;
  name: string;
  ownerNodeSeq: number;
  actionHandler?: ActionHandler;
  parameters: ActionParameter[];
}
