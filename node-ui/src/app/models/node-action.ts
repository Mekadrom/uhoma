import { Node } from './node';
import { ActionParameter } from './action-parameter';

export interface NodeAction {
  actionSeq?: number;
  name: string;
  ownerNode: Node;
  handler: string;
  parameters: ActionParameter[];
}
