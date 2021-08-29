import { Node, ActionHandler, ActionParameter } from '../models';

export interface Action {
  actionSeq?: number;
  name: string;
  ownerNode: Node;
  actionHandler?: ActionHandler;
  parameters: ActionParameter[];
}
